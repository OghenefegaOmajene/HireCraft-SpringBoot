package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.EscrowReleaseRequest;
import HireCraft.com.SpringBoot.dtos.requests.PaymentInitiationRequest;
import HireCraft.com.SpringBoot.dtos.requests.SubaccountCreationRequest;
import HireCraft.com.SpringBoot.dtos.requests.WebhookRequest;
import HireCraft.com.SpringBoot.dtos.response.PaymentInitiationResponse;
import HireCraft.com.SpringBoot.dtos.response.PaymentVerificationResponse;
import HireCraft.com.SpringBoot.enums.*;
import HireCraft.com.SpringBoot.models.*;
import HireCraft.com.SpringBoot.repository.*;
import HireCraft.com.SpringBoot.services.PaymentService;
import HireCraft.com.SpringBoot.services.PaystackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service

public class PaymentServiceImpl implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final SplitPaymentRepository splitPaymentRepository;
    private final EscrowPaymentRepository escrowPaymentRepository;
    private final ProviderSubaccountRepository providerSubaccountRepository;
    private final BookingRepository bookingRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository serviceProviderProfileRepository;
    private final PaystackService paystackService;

    public PaymentServiceImpl(PaymentTransactionRepository paymentTransactionRepository, SplitPaymentRepository splitPaymentRepository, EscrowPaymentRepository escrowPaymentRepository, ProviderSubaccountRepository providerSubaccountRepository, BookingRepository bookingRepository, ClientProfileRepository clientProfileRepository, ServiceProviderProfileRepository serviceProviderProfileRepository, PaystackService paystackService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.splitPaymentRepository = splitPaymentRepository;
        this.escrowPaymentRepository = escrowPaymentRepository;
        this.providerSubaccountRepository = providerSubaccountRepository;
        this.bookingRepository = bookingRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.serviceProviderProfileRepository = serviceProviderProfileRepository;
        this.paystackService = paystackService;
    }

    private static final BigDecimal PLATFORM_FEE_PERCENTAGE = new BigDecimal("0.03" +
            ""); // 10%

//    @Override
//    @Transactional
//    public PaymentInitiationResponse initiatePayment(PaymentInitiationRequest request) {
//        try {
//            // Validate booking exists
//            Booking booking = bookingRepository.findById(request.getBookingId())
//                    .orElseThrow(() -> new RuntimeException("Booking not found"));
//
//            // Generate unique reference
//            String reference = "HIRE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
//
//            // Create transaction record
//            PaymentTransaction transaction = createTransaction(
//                    request.getBookingId(),
//                    request.getAmount(),
//                    request.getEmail(),
//                    TransactionType.BOOKING_PAYMENT
//            );
//            transaction.setReference(reference);
//            transaction = paymentTransactionRepository.save(transaction);
//
//            // Initialize payment with Paystack
//            Map<String, Object> paystackResponse = paystackService.initializeTransaction(
//                    request.getEmail(),
//                    request.getAmount(),
//                    reference,
//                    request.getCallbackUrl()
//            );
//
//            if (paystackResponse.get("status").equals(true)) {
//                Map<String, Object> data = (Map<String, Object>) paystackResponse.get("data");
//
//                // Update transaction with Paystack reference
//                transaction.setPaystackReference((String) data.get("reference"));
//                paymentTransactionRepository.save(transaction);
//
//                // Create split payment if needed
//                if (!request.isUseEscrow()) {
//                    createSplitPayment(transaction, PLATFORM_FEE_PERCENTAGE);
//                } else {
//                    // Create escrow payment
//                    createEscrowPayment(transaction);
//                }
//
//                return new PaymentInitiationResponse(
//                        true,
//                        reference,
//                        (String) data.get("authorization_url"),
//                        (String) data.get("access_code"),
//                        "Payment initiated successfully"
//                );
//            } else {
//                transaction.setStatus(TransactionStatus.FAILED);
//                paymentTransactionRepository.save(transaction);
//
//                return new PaymentInitiationResponse(
//                        false,
//                        reference,
//                        null,
//                        null,
//                        "Payment initiation failed: " + paystackResponse.get("message")
//                );
//            }
//        } catch (Exception e) {
//            log.error("Error initiating payment: ", e);
//            return new PaymentInitiationResponse(
//                    false,
//                    null,
//                    null,
//                    null,
//                    "Payment initiation failed: " + e.getMessage()
//            );
//        }
//    }

    @Override
    @Transactional
    public PaymentInitiationResponse initiatePayment(PaymentInitiationRequest request) {
        try {
            // Validate booking exists
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Generate unique reference FIRST
            String reference = "HIRE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

            // Create transaction record with reference
            PaymentTransaction transaction = createTransactionWithReference(
                    request.getBookingId(),
                    request.getAmount(),
                    request.getEmail(),
                    TransactionType.BOOKING_PAYMENT,
                    reference  // Pass the reference to the creation method
            );
            transaction = paymentTransactionRepository.save(transaction);

            // Initialize payment with Paystack
            Map<String, Object> paystackResponse = paystackService.initializeTransaction(
                    request.getEmail(),
                    request.getAmount(),
                    reference,
                    request.getCallbackUrl()
            );

            if (paystackResponse.get("status").equals(true)) {
                Map<String, Object> data = (Map<String, Object>) paystackResponse.get("data");

                // Update transaction with Paystack reference
                transaction.setPaystackReference((String) data.get("reference"));
                paymentTransactionRepository.save(transaction);

                // Create split payment if needed
                if (!request.isUseEscrow()) {
                    createSplitPayment(transaction, PLATFORM_FEE_PERCENTAGE);
                } else {
                    // Create escrow payment
                    createEscrowPayment(transaction);
                }

                return new PaymentInitiationResponse(
                        true,
                        reference,
                        (String) data.get("authorization_url"),
                        (String) data.get("access_code"),
                        "Payment initiated successfully"
                );
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
                paymentTransactionRepository.save(transaction);

                return new PaymentInitiationResponse(
                        false,
                        reference,
                        null,
                        null,
                        "Payment initiation failed: " + paystackResponse.get("message")
                );
            }
        } catch (Exception e) {
            log.error("Error initiating payment: ", e);
            return new PaymentInitiationResponse(
                    false,
                    null,
                    null,
                    null,
                    "Payment initiation failed: " + e.getMessage()
            );
        }
    }

    // Create a new method that accepts reference as parameter
    @Override
    @Transactional
    public PaymentTransaction createTransactionWithReference(Long bookingId, BigDecimal amount, String email, TransactionType type, String reference) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Calculate platform fee and provider amount
        BigDecimal platformFee = amount.multiply(PLATFORM_FEE_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal providerAmount = amount.subtract(platformFee);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setReference(reference);  // Set the reference here
        transaction.setAmount(amount);
        transaction.setDescription("Payment for booking #" + bookingId);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setType(type);
        transaction.setClient(booking.getClientProfile());
        transaction.setProvider(booking.getProviderProfile());
        transaction.setBooking(booking);
        transaction.setPlatformFee(platformFee);
        transaction.setProviderAmount(providerAmount);

        return paymentTransactionRepository.save(transaction);
    }

    // Keep the original method for backward compatibility, but generate reference internally
    @Override
    @Transactional
    public PaymentTransaction createTransaction(Long bookingId, BigDecimal amount, String email, TransactionType type) {
        String reference = "HIRE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return createTransactionWithReference(bookingId, amount, email, type, reference);
    }

    @Override
    @Transactional
    public PaymentVerificationResponse verifyPayment(String reference) {
        try {
            PaymentTransaction transaction = paymentTransactionRepository.findByReference(reference)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            // Verify with Paystack
            Map<String, Object> paystackResponse = paystackService.verifyTransaction(reference);

            if (paystackResponse.get("status").equals(true)) {
                Map<String, Object> data = (Map<String, Object>) paystackResponse.get("data");
                String status = (String) data.get("status");

                if ("success".equals(status)) {
                    // Update transaction
                    transaction.setStatus(TransactionStatus.SUCCESS);
                    transaction.setPaystackTransactionId(data.get("id").toString());
                    transaction.setGatewayResponse((String) data.get("gateway_response"));
                    transaction.setPaidAt(LocalDateTime.now());

                    // Update authorization if available
                    if (data.containsKey("authorization")) {
                        Map<String, Object> authorization = (Map<String, Object>) data.get("authorization");
                        transaction.setAuthorizationCode((String) authorization.get("authorization_code"));
                    }

                    paymentTransactionRepository.save(transaction);

                    // Update booking status
                    Booking booking = transaction.getBooking();
                    if (booking != null) {
                        booking.setStatus(BookingStatus.COMPLETED);
                        bookingRepository.save(booking);
                    }

                    // Handle split payment or escrow
                    handlePaymentCompletion(transaction);

                    return new PaymentVerificationResponse(
                            true,
                            reference,
                            transaction.getAmount(),
                            "SUCCESS",
                            transaction.getGatewayResponse(),
                            transaction.getPaidAt(),
                            "Payment verified successfully"
                    );
                } else {
                    transaction.setStatus(TransactionStatus.FAILED);
                    transaction.setGatewayResponse((String) data.get("gateway_response"));
                    paymentTransactionRepository.save(transaction);

                    return new PaymentVerificationResponse(
                            false,
                            reference,
                            transaction.getAmount(),
                            "FAILED",
                            transaction.getGatewayResponse(),
                            null,
                            "Payment verification failed"
                    );
                }
            } else {
                return new PaymentVerificationResponse(
                        false,
                        reference,
                        transaction.getAmount(),
                        "FAILED",
                        null,
                        null,
                        "Payment verification failed: " + paystackResponse.get("message")
                );
            }
        } catch (Exception e) {
            log.error("Error verifying payment: ", e);
            return new PaymentVerificationResponse(
                    false,
                    reference,
                    null,
                    "ERROR",
                    null,
                    null,
                    "Payment verification error: " + e.getMessage()
            );
        }
    }

//    @Override
//    @Transactional
//    public PaymentTransaction createTransaction(Long bookingId, BigDecimal amount, String email, TransactionType type) {
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new RuntimeException("Booking not found"));
//
//        // Calculate platform fee and provider amount
//        BigDecimal platformFee = amount.multiply(PLATFORM_FEE_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
//        BigDecimal providerAmount = amount.subtract(platformFee);
//
//        PaymentTransaction transaction = new PaymentTransaction();
//        transaction.setAmount(amount);
//        transaction.setDescription("Payment for booking #" + bookingId);
//        transaction.setStatus(TransactionStatus.PENDING);
//        transaction.setType(type);
//        transaction.setClient(booking.getClientProfile());
//        transaction.setProvider(booking.getProviderProfile());
//        transaction.setBooking(booking);
//        transaction.setPlatformFee(platformFee);
//        transaction.setProviderAmount(providerAmount);
//
//        return paymentTransactionRepository.save(transaction);
//    }

    @Override
    @Transactional
    public PaymentTransaction updateTransactionStatus(String reference, TransactionStatus status) {
        PaymentTransaction transaction = paymentTransactionRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setStatus(status);
        return paymentTransactionRepository.save(transaction);
    }

    @Override
    public PaymentTransaction getTransactionByReference(String reference) {
        return paymentTransactionRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    public List<PaymentTransaction> getTransactionsByClient(Long clientId) {
        return paymentTransactionRepository.findByClientIdAndStatus(clientId, TransactionStatus.SUCCESS);
    }

    @Override
    public List<PaymentTransaction> getTransactionsByProvider(Long providerId) {
        return paymentTransactionRepository.findByProviderIdAndStatus(providerId, TransactionStatus.SUCCESS);
    }

    @Override
    @Transactional
    public SplitPayment createSplitPayment(PaymentTransaction transaction, BigDecimal platformPercentage) {
        SplitPayment splitPayment = new SplitPayment();
        splitPayment.setTransaction(transaction);
        splitPayment.setTotalAmount(transaction.getAmount());
        splitPayment.setPlatformPercentage(platformPercentage);
        splitPayment.setPlatformAmount(transaction.getPlatformFee());
        splitPayment.setProviderAmount(transaction.getProviderAmount());
        splitPayment.setStatus(SplitStatus.PENDING);

        return splitPaymentRepository.save(splitPayment);
    }

    @Override
    @Transactional
    public SplitPayment updateSplitPaymentStatus(Long transactionId, SplitStatus status) {
        SplitPayment splitPayment = splitPaymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Split payment not found"));

        splitPayment.setStatus(status);
        return splitPaymentRepository.save(splitPayment);
    }

    @Override
    @Transactional
    public EscrowPayment createEscrowPayment(PaymentTransaction transaction) {
        EscrowPayment escrowPayment = new EscrowPayment();
        escrowPayment.setTransaction(transaction);
        escrowPayment.setEscrowAmount(transaction.getProviderAmount());
        escrowPayment.setStatus(EscrowStatus.HELD);
        escrowPayment.setAutoReleaseDate(LocalDateTime.now().plusDays(7)); // Auto-release after 7 days

        return escrowPaymentRepository.save(escrowPayment);
    }

    @Override
    @Transactional
    public EscrowPayment releaseEscrowPayment(EscrowReleaseRequest request) {
        EscrowPayment escrowPayment = escrowPaymentRepository.findByTransactionId(request.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Escrow payment not found"));

        if (escrowPayment.getStatus() != EscrowStatus.HELD) {
            throw new RuntimeException("Escrow payment is not in HELD status");
        }

        escrowPayment.setStatus(EscrowStatus.RELEASED);
        escrowPayment.setReleasedAt(LocalDateTime.now());
        escrowPayment.setReleaseReason(request.getReleaseReason());
        escrowPayment.setReleasedBy(request.getReleasedBy());

        return escrowPaymentRepository.save(escrowPayment);
    }

    @Override
    public List<EscrowPayment> getEscrowsReadyForAutoRelease() {
        return escrowPaymentRepository.findEscrowsReadyForAutoRelease(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void processAutoRelease() {
        List<EscrowPayment> escrowsToRelease = getEscrowsReadyForAutoRelease();

        for (EscrowPayment escrow : escrowsToRelease) {
            try {
                escrow.setStatus(EscrowStatus.RELEASED);
                escrow.setReleasedAt(LocalDateTime.now());
                escrow.setReleaseReason("Auto-released after completion period");
                escrow.setReleasedBy("SYSTEM");
                escrowPaymentRepository.save(escrow);

                log.info("Auto-released escrow payment for transaction: {}", escrow.getTransaction().getId());
            } catch (Exception e) {
                log.error("Error auto-releasing escrow payment: {}", escrow.getId(), e);
            }
        }
    }

    @Override
    @Transactional
    public ProviderSubaccount createProviderSubaccount(SubaccountCreationRequest request) {
        // Check if provider already has a subaccount
        if (providerSubaccountRepository.existsByProviderId(request.getProviderId())) {
            throw new RuntimeException("Provider already has a subaccount");
        }

        ServiceProviderProfile provider = serviceProviderProfileRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        try {
            // Create subaccount with Paystack
            Map<String, Object> paystackResponse = paystackService.createSubaccount(
                    request.getBusinessName(),
                    request.getSettlementBank(),
                    request.getAccountNumber(),
                    request.getPercentageCharge()
            );

            if (paystackResponse.get("status").equals(true)) {
                Map<String, Object> data = (Map<String, Object>) paystackResponse.get("data");

                ProviderSubaccount subaccount = new ProviderSubaccount();
                subaccount.setProvider(provider);
                subaccount.setSubaccountCode((String) data.get("subaccount_code"));
                subaccount.setBusinessName(request.getBusinessName());
                subaccount.setSettlementBank(request.getSettlementBank());
                subaccount.setAccountNumber(request.getAccountNumber());
                subaccount.setPercentageCharge(request.getPercentageCharge());
                subaccount.setActive(true);

                return providerSubaccountRepository.save(subaccount);
            } else {
                throw new RuntimeException("Failed to create Paystack subaccount: " + paystackResponse.get("message"));
            }
        } catch (Exception e) {
            log.error("Error creating provider subaccount: ", e);
            throw new RuntimeException("Failed to create provider subaccount: " + e.getMessage());
        }
    }

    @Override
    public ProviderSubaccount getProviderSubaccount(Long providerId) {
        return providerSubaccountRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("Provider subaccount not found"));
    }

    @Override
    @Transactional
    public void updateSubaccountStatus(Long providerId, boolean isActive) {
        ProviderSubaccount subaccount = getProviderSubaccount(providerId);
        subaccount.setActive(isActive);
        providerSubaccountRepository.save(subaccount);
    }

    @Override
    @Transactional
    public void handleWebhook(WebhookRequest webhookRequest) {
        try {
            String event = webhookRequest.getEvent();
            Map<String, Object> data = (Map<String, Object>) webhookRequest.getData();

            switch (event) {
                case "charge.success":
                    handleChargeSuccess(data);
                    break;
                case "charge.failed":
                    handleChargeFailed(data);
                    break;
                case "transfer.success":
                    handleTransferSuccess(data);
                    break;
                case "transfer.failed":
                    handleTransferFailed(data);
                    break;
                default:
                    log.info("Unhandled webhook event: {}", event);
            }
        } catch (Exception e) {
            log.error("Error handling webhook: ", e);
        }
    }

    private void handleChargeSuccess(Map<String, Object> data) {
        String reference = (String) data.get("reference");
        PaymentTransaction transaction = paymentTransactionRepository.findByReference(reference)
                .orElse(null);

        if (transaction != null && transaction.getStatus() == TransactionStatus.PENDING) {
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setPaystackTransactionId(data.get("id").toString());
            transaction.setGatewayResponse((String) data.get("gateway_response"));
            transaction.setPaidAt(LocalDateTime.now());
            paymentTransactionRepository.save(transaction);

            // Handle post-payment processing
            handlePaymentCompletion(transaction);

            log.info("Payment confirmed via webhook: {}", reference);
        }
    }

    private void handleChargeFailed(Map<String, Object> data) {
        String reference = (String) data.get("reference");
        PaymentTransaction transaction = paymentTransactionRepository.findByReference(reference)
                .orElse(null);

        if (transaction != null && transaction.getStatus() == TransactionStatus.PENDING) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setGatewayResponse((String) data.get("gateway_response"));
            paymentTransactionRepository.save(transaction);

            log.info("Payment failed via webhook: {}", reference);
        }
    }

    private void handleTransferSuccess(Map<String, Object> data) {
        // Handle successful transfer to provider
        log.info("Transfer successful: {}", data.get("reference"));
    }

    private void handleTransferFailed(Map<String, Object> data) {
        // Handle failed transfer to provider
        log.error("Transfer failed: {}", data.get("reference"));
    }

    private void handlePaymentCompletion(PaymentTransaction transaction) {
        // Update split payment status if exists
        splitPaymentRepository.findByTransactionId(transaction.getId())
                .ifPresent(splitPayment -> {
                    splitPayment.setStatus(SplitStatus.COMPLETED);
                    splitPaymentRepository.save(splitPayment);
                });

        // Update booking status
        if (transaction.getBooking() != null) {
            Booking booking = transaction.getBooking();
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);
        }
    }

    @Override
    public BigDecimal getTotalEarningsByProvider(Long providerId) {
        return paymentTransactionRepository.getTotalEarningsByProvider(providerId);
    }

    @Override
    public BigDecimal getTotalPlatformFeeByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentTransactionRepository.getTotalPlatformFeeByDateRange(startDate, endDate);
    }

    @Override
    public List<PaymentTransaction> getTransactionsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Determine if user is client or provider and fetch accordingly
        List<PaymentTransaction> clientTransactions = paymentTransactionRepository.findByClientIdAndDateRange(userId, startDate, endDate);
        if (!clientTransactions.isEmpty()) {
            return clientTransactions;
        }
        return paymentTransactionRepository.findByProviderIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public List<PaymentTransaction> getTransactionsByROLE_PROVIDER(Long roleProviderId) {
        return List.of();
    }

    @Override
    public List<PaymentTransaction> getTransactionsByROLE_CLIENT(Long roleClientId) {
        return List.of();
    }

    @Override
    public BigDecimal getTotalEarningsByROLE_PROVIDER(Long roleProviderId) {
        return null;
    }
}