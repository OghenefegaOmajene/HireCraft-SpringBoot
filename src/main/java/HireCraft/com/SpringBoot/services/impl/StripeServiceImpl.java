//package HireCraft.com.SpringBoot.services.impl;
//
//import HireCraft.com.SpringBoot.enums.TransactionStatus;
//import HireCraft.com.SpringBoot.models.ClientProfile;
//import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
//import HireCraft.com.SpringBoot.models.Transaction;
//import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
//import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
//import HireCraft.com.SpringBoot.repository.TransactionRepository;
//import HireCraft.com.SpringBoot.services.StripeService;
//import com.stripe.exception.StripeException;
//import com.stripe.model.*;
//import com.stripe.param.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//
//@Service
//public class StripeServiceImpl implements StripeService {
//
//    @Autowired
//    private ServiceProviderProfileRepository providerRepository;
//
//    @Autowired
//    private ClientProfileRepository clientRepository;
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    @Value("${hirecraft.payment.platform-fee-percentage}")
//    private Double platformFeePercentage;
//
//    @Value("${hirecraft.payment.currency}")
//    private String currency;
//
//    @Value("${hirecraft.payment.minimum-amount}")
//    private BigDecimal minimumAmount;
//
//    @Override
//    public String createConnectAccount(ServiceProviderProfile provider) throws StripeException {
//        if (provider.getStripeAccountId() != null) {
//            return provider.getStripeAccountId();
//        }
//
//        AccountCreateParams params = AccountCreateParams.builder()
//                .setType(AccountCreateParams.Type.EXPRESS)
//                .setCountry("US") // You might want to make this configurable
//                .setEmail(provider.getUser().getEmail())
//                .setIndividual(
//                        AccountCreateParams.Individual.builder()
//                                .setFirstName(provider.getUser().getFirstName())
//                                .setLastName(provider.getUser().getLastName())
//                                .setEmail(provider.getUser().getEmail())
//                                .build()
//                )
//                .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
//                .setCapabilities(
//                        AccountCreateParams.Capabilities.builder()
//                                .setCardPayments(
//                                        AccountCreateParams.Capabilities.CardPayments.builder()
//                                                .setRequested(true)
//                                                .build()
//                                )
//                                .setTransfers(
//                                        AccountCreateParams.Capabilities.Transfers.builder()
//                                                .setRequested(true)
//                                                .build()
//                                )
//                                .build()
//                )
//                .setBusinessProfile(
//                        AccountCreateParams.BusinessProfile.builder()
//                                .setName(provider.getUser().getFirstName() + " " + provider.getUser().getLastName())
//                                .setProductDescription(provider.getOccupation())
//                                .setUrl(provider.getCvUrl())
//                                .build()
//                )
//                .build();
//
//        Account account = Account.create(params);
//
//        provider.setStripeAccountId(account.getId());
//        providerRepository.save(provider);
//
//        return account.getId();
//    }
//
//    @Override
//    public String createOnboardingLink(String accountId, String refreshUrl, String returnUrl) throws StripeException {
//        AccountLinkCreateParams params = AccountLinkCreateParams.builder()
//                .setAccount(accountId)
//                .setRefreshUrl(refreshUrl != null ? refreshUrl : "http://localhost:8080/stripe/reauth")
//                .setReturnUrl(returnUrl != null ? returnUrl : "http://localhost:8080/stripe/return")
//                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
//                .build();
//
//        AccountLink accountLink = AccountLink.create(params);
//        return accountLink.getUrl();
//    }
//
//    @Override
//    public String createCustomer(ClientProfile client) throws StripeException {
//        if (client.getStripeCustomerId() != null) {
//            return client.getStripeCustomerId();
//        }
//
//        CustomerCreateParams params = CustomerCreateParams.builder()
//                .setEmail(client.getUser().getEmail())
//                .setName(client.getUser().getFirstName() + " " + client.getUser().getLastName())
//                .setDescription("Client - " + client.getJobTitle())
//                .build();
//
//        Customer customer = Customer.create(params);
//
//        client.setStripeCustomerId(customer.getId());
//        clientRepository.save(client);
//
//        return customer.getId();
//    }
//
//    @Override
//    public PaymentIntent createPaymentIntent(Long clientId, Long providerId,
//                                             BigDecimal amount, String description) throws StripeException {
//
//        // Validate minimum amount
//        if (amount.compareTo(minimumAmount) < 0) {
//            throw new IllegalArgumentException("Amount must be at least $" + minimumAmount);
//        }
//
//        ClientProfile client = clientRepository.findById(clientId)
//                .orElseThrow(() -> new RuntimeException("Client not found"));
//        ServiceProviderProfile provider = providerRepository.findById(providerId)
//                .orElseThrow(() -> new RuntimeException("Provider not found"));
//
//        // Ensure client has Stripe customer ID
//        if (client.getStripeCustomerId() == null) {
//            createCustomer(client);
//        }
//
//        // Ensure provider has Stripe account and is onboarded
//        if (provider.getStripeAccountId() == null) {
//            throw new RuntimeException("Provider must create Connect account first");
//        }
//
//        if (!isProviderOnboardingComplete(provider.getStripeAccountId())) {
//            throw new RuntimeException("Provider must complete onboarding first");
//        }
//
//        // Calculate fees
//        BigDecimal platformFee = calculatePlatformFee(amount);
//        BigDecimal providerAmount = amount.subtract(platformFee);
//
//        // Create transaction record
//        Transaction transaction = Transaction.builder()
//                .client(client)
//                .provider(provider)
//                .amount(amount)
//                .platformFee(platformFee)
//                .providerAmount(providerAmount)
//                .description(description)
//                .status(TransactionStatus.PENDING)
//                .build();
//
//        transaction = transactionRepository.save(transaction);
//
//        // Create payment intent
//        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
//                .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue()) // Convert to cents
//                .setCurrency(currency)
//                .setCustomer(client.getStripeCustomerId())
//                .setApplicationFeeAmount(platformFee.multiply(BigDecimal.valueOf(100)).longValue())
//                .setTransferData(
//                        PaymentIntentCreateParams.TransferData.builder()
//                                .setDestination(provider.getStripeAccountId())
//                                .build()
//                )
//                .putMetadata("transaction_id", transaction.getId().toString())
//                .putMetadata("client_id", clientId.toString())
//                .putMetadata("provider_id", providerId.toString())
//                .setDescription(description)
//                .setStatementDescriptor("HIRECRAFT")
//                .build();
//
//        PaymentIntent paymentIntent = PaymentIntent.create(params);
//
//        transaction.setStripePaymentIntentId(paymentIntent.getId());
//        transactionRepository.save(transaction);
//
//        return paymentIntent;
//    }
//
//    @Override
//    public Balance getAccountBalance() throws StripeException {
//        return Balance.retrieve();
//    }
//
//    @Override
//    public Account getConnectAccount(String accountId) throws StripeException {
//        return Account.retrieve(accountId);
//    }
//
//    @Override
//    public boolean isProviderOnboardingComplete(String accountId) throws StripeException {
//        Account account = Account.retrieve(accountId);
//        return account.getChargesEnabled() && account.getPayoutsEnabled();
//    }
//
//    @Override
//    public BigDecimal calculatePlatformFee(BigDecimal amount) {
//        BigDecimal feePercentage = BigDecimal.valueOf(platformFeePercentage / 100.0);
//        return amount.multiply(feePercentage).setScale(2, RoundingMode.HALF_UP);
//    }
//
//    @Override
//    public void refundPayment(String paymentIntentId, BigDecimal amount) throws StripeException {
//        RefundCreateParams params = RefundCreateParams.builder()
//                .setPaymentIntent(paymentIntentId)
//                .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
//                .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
//                .build();
//
//        Refund.create(params);
//
//        // Update transaction status
//        Transaction transaction = transactionRepository.findByStripePaymentIntentId(paymentIntentId)
//                .orElseThrow(() -> new RuntimeException("Transaction not found"));
//        transaction.setStatus(TransactionStatus.REFUNDED);
//        transactionRepository.save(transaction);
//    }
//}
