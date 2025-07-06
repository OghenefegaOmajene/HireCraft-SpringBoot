//package HireCraft.com.SpringBoot.services.impl;
//
//import HireCraft.com.SpringBoot.dtos.PaymentBreakdown;
//import HireCraft.com.SpringBoot.dtos.requests.PaymentRequest;
//import HireCraft.com.SpringBoot.dtos.response.PaymentResponse;
//import HireCraft.com.SpringBoot.dtos.response.StripePaymentResponse;
//import HireCraft.com.SpringBoot.enums.PaymentStatus;
//import HireCraft.com.SpringBoot.exceptions.PaymentNotFoundException;
//import HireCraft.com.SpringBoot.exceptions.PaymentProcessingException;
//import HireCraft.com.SpringBoot.models.Payment;
//import HireCraft.com.SpringBoot.models.User;
//import HireCraft.com.SpringBoot.processor.StripePaymentProcessor;
//import HireCraft.com.SpringBoot.repository.PaymentRepository;
//import HireCraft.com.SpringBoot.repository.UserRepository;
//import HireCraft.com.SpringBoot.services.PaymentService;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.transaction.annotation.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//@Slf4j
//public class PaymentServiceImpl implements PaymentService {
//
//    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
//    private final PaymentRepository paymentRepository;
//    private final StripePaymentProcessor stripePaymentProcessor;
//    private final UserRepository userRepository;
//
//    @Value("${hirecraft.payment.platform-fee-percentage}")
//    private BigDecimal platformFeePercentage;
//
//    public PaymentServiceImpl(PaymentRepository paymentRepository,
//                              StripePaymentProcessor stripePaymentProcessor, UserRepository userRepository) {
//        this.paymentRepository = paymentRepository;
//        this.stripePaymentProcessor = stripePaymentProcessor;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public PaymentResponse processPayment(PaymentRequest request, UserDetails userDetails) {
//        log.info("Processing payment with UserDetails: {}", userDetails.getUsername());
//
//        // 🧠 Find user in your database based on email or username
//        User user = userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
//
//        if (user.getRole().name().equals("ROLE_CLIENT")) {
//            request.setClientId(user.getId());
//        } else if (user.getRole().name().equals("ROLE_PROVIDER")) {
//            request.setProviderId(user.getId());
//        }
//
//        return processPayment(request);
//    }
//
//
//    @Override
//    public PaymentResponse processPayment(PaymentRequest request) {
//        log.info("Processing payment for client: {} to provider: {}",
//                request.getClientId(), request.getProviderId());
//
//        try {
//            // Calculate payment breakdown
//            PaymentBreakdown breakdown = calculatePaymentBreakdown(request.getAmount());
//
//            // Create payment record
//            Payment payment = Payment.builder()
//                    .clientId(request.getClientId())
//                    .providerId(request.getProviderId())
//                    .bookingId(request.getBookingId())
//                    .totalAmount(breakdown.getTotalAmount())
//                    .platformFeePercentage(breakdown.getPlatformFeePercentage())
//                    .platformFee(breakdown.getPlatformFee())
//                    .providerAmount(breakdown.getProviderAmount())
//                    .status(PaymentStatus.PENDING)
//                    .paymentMethod(request.getPaymentMethod())
//                    .description(request.getDescription())
//                    .build();
//
//            // Save payment
//            payment = paymentRepository.save(payment);
//
//            // Process with payment processor
//            StripePaymentResponse stripeResponse = stripePaymentProcessor.processPayment(
//                    request.getPaymentMethodId(),
//                    breakdown.getTotalAmount(),
//                    breakdown.getPlatformFee(),
//                    request.getProviderId()
//            );
//
//            // Update payment with result
//            payment.setExternalTransactionId(stripeResponse.getTransactionId());
//            payment.setStatus(stripeResponse.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
//            payment = paymentRepository.save(payment);
//
//            log.info("Payment processed successfully. Payment ID: {}, Status: {}",
//                    payment.getId(), payment.getStatus());
//
//            return PaymentResponse.fromEntity(payment);
//
//        } catch (Exception e) {
//            log.error("Failed to process payment", e);
//            throw new PaymentProcessingException("Failed to process payment: " + e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public PaymentResponse getPaymentById(Long paymentId) {
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
//        return PaymentResponse.fromEntity(payment);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<PaymentResponse> getClientPayments(Long clientId) {
//        return paymentRepository.findByClientIdOrderByCreatedAtDesc(clientId)
//                .stream()
//                .map(PaymentResponse::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<PaymentResponse> getProviderPayments(Long providerId) {
//        return paymentRepository.findByProviderIdOrderByCreatedAtDesc(providerId)
//                .stream()
//                .map(PaymentResponse::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public PaymentBreakdown calculatePaymentBreakdown(BigDecimal amount) {
//        return PaymentBreakdown.calculate(amount, platformFeePercentage);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public BigDecimal calculateProviderEarnings(Long providerId, LocalDateTime startDate, LocalDateTime endDate) {
//        BigDecimal earnings = paymentRepository.calculateProviderEarnings(providerId, startDate, endDate);
//        return earnings != null ? earnings : BigDecimal.ZERO;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public BigDecimal calculatePlatformRevenue(LocalDateTime startDate, LocalDateTime endDate) {
//        BigDecimal revenue = paymentRepository.calculateTotalPlatformRevenue(startDate, endDate);
//        return revenue != null ? revenue : BigDecimal.ZERO;
//    }
//
//    // In PaymentService interface and implementation
//    @Override
//    @Transactional(readOnly = true)
//    public List<PaymentResponse> getBookingPayments(Long bookingId) {
//        return paymentRepository.findByBookingIdOrderByCreatedAtDesc(bookingId)
//                .stream()
//                .map(PaymentResponse::fromEntity)
//                .collect(Collectors.toList());
//    }
//}

package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.OnboardingRequest;
import HireCraft.com.SpringBoot.dtos.requests.PaymentRequest;
import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.services.PaymentService;
import HireCraft.com.SpringBoot.services.StripeService;
import com.stripe.model.Account;
import com.stripe.model.Balance;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private ServiceProviderProfileRepository providerRepository;

    @Autowired
    private ClientProfileRepository clientRepository;

    @Override
    public Map<String, Object> createConnectAccount(OnboardingRequest request) throws Exception {
        ServiceProviderProfile provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        String accountId = stripeService.createConnectAccount(provider);
        String onboardingUrl = stripeService.createOnboardingLink(
                accountId,
                request.getRefreshUrl(),
                request.getReturnUrl()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("account_id", accountId);
        response.put("onboarding_url", onboardingUrl);
        response.put("message", "Connect account created successfully");

        return response;
    }

    @Override
    public Map<String, Object> createPaymentIntent(PaymentRequest request) throws Exception {
        PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                request.getClientId(),
                request.getProviderId(),
                request.getAmount(),
                request.getDescription()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("client_secret", paymentIntent.getClientSecret());
        response.put("payment_intent_id", paymentIntent.getId());
        response.put("amount", request.getAmount());
        response.put("platform_fee", stripeService.calculatePlatformFee(request.getAmount()));

        return response;
    }

    @Override
    public Balance getAccountBalance() throws Exception {
        return stripeService.getAccountBalance();
    }

    @Override
    public Map<String, Object> getConnectAccountDetails(String accountId) throws Exception {
        Account account = stripeService.getConnectAccount(accountId);
        boolean isOnboardingComplete = stripeService.isProviderOnboardingComplete(accountId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("account", account);
        response.put("onboarding_complete", isOnboardingComplete);
        response.put("charges_enabled", account.getChargesEnabled());
        response.put("payouts_enabled", account.getPayoutsEnabled());

        return response;
    }

    @Override
    public Map<String, Object> getProviderOnboardingStatus(Long providerId) throws Exception {
        ServiceProviderProfile provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("has_stripe_account", provider.getStripeAccountId() != null);

        if (provider.getStripeAccountId() != null) {
            boolean isComplete = stripeService.isProviderOnboardingComplete(provider.getStripeAccountId());
            response.put("onboarding_complete", isComplete);
        } else {
            response.put("onboarding_complete", false);
        }

        return response;
    }
}