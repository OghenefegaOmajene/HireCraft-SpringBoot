package HireCraft.com.SpringBoot.services;


import HireCraft.com.SpringBoot.dtos.requests.EscrowReleaseRequest;
import HireCraft.com.SpringBoot.dtos.requests.PaymentInitiationRequest;
import HireCraft.com.SpringBoot.dtos.requests.SubaccountCreationRequest;
import HireCraft.com.SpringBoot.dtos.requests.WebhookRequest;
import HireCraft.com.SpringBoot.dtos.response.PaymentInitiationResponse;
import HireCraft.com.SpringBoot.dtos.response.PaymentVerificationResponse;
import HireCraft.com.SpringBoot.enums.SplitStatus;
import HireCraft.com.SpringBoot.enums.TransactionStatus;
import HireCraft.com.SpringBoot.enums.TransactionType;
import HireCraft.com.SpringBoot.models.EscrowPayment;
import HireCraft.com.SpringBoot.models.PaymentTransaction;
import HireCraft.com.SpringBoot.models.ProviderSubaccount;
import HireCraft.com.SpringBoot.models.SplitPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    // Payment initiation and verification
    PaymentInitiationResponse initiatePayment(PaymentInitiationRequest request);
    PaymentVerificationResponse verifyPayment(String reference);

    // Transaction management
    PaymentTransaction createTransaction(Long bookingId, BigDecimal amount, String email, TransactionType type);
    PaymentTransaction updateTransactionStatus(String reference, TransactionStatus status);
    PaymentTransaction getTransactionByReference(String reference);
    List<PaymentTransaction> getTransactionsByClient(Long clientId);
    List<PaymentTransaction> getTransactionsByProvider(Long providerId);

    // Split payments
    SplitPayment createSplitPayment(PaymentTransaction transaction, BigDecimal platformPercentage);
    SplitPayment updateSplitPaymentStatus(Long transactionId, SplitStatus status);

    // Escrow management
    EscrowPayment createEscrowPayment(PaymentTransaction transaction);
    EscrowPayment releaseEscrowPayment(EscrowReleaseRequest request);
    List<EscrowPayment> getEscrowsReadyForAutoRelease();
    void processAutoRelease();

    // Provider subaccounts
    ProviderSubaccount createProviderSubaccount(SubaccountCreationRequest request);
    ProviderSubaccount getProviderSubaccount(Long providerId);
    void updateSubaccountStatus(Long providerId, boolean isActive);

    // Webhook handling
    void handleWebhook(WebhookRequest webhookRequest);

    // Analytics and reporting
    BigDecimal getTotalEarningsByProvider(Long providerId);
    BigDecimal getTotalPlatformFeeByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<PaymentTransaction> getTransactionsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<PaymentTransaction> getTransactionsByROLE_PROVIDER(Long roleProviderId);

    List<PaymentTransaction> getTransactionsByROLE_CLIENT(Long roleClientId);

    BigDecimal getTotalEarningsByROLE_PROVIDER(Long roleProviderId);
}