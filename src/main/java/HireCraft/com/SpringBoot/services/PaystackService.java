package HireCraft.com.SpringBoot.services;

import java.math.BigDecimal;
import java.util.Map;

public interface PaystackService {

    // Core payment operations
    Map<String, Object> initializeTransaction(String email, BigDecimal amount, String reference, String callbackUrl);
    Map<String, Object> verifyTransaction(String reference);

    // Subaccount management
    Map<String, Object> createSubaccount(String businessName, String settlementBank, String accountNumber, BigDecimal percentageCharge);
    Map<String, Object> updateSubaccount(String subaccountCode, Map<String, Object> updateData);

    // Split payment operations
    Map<String, Object> createSplitPayment(String name, String type, String currency, String[] subaccounts, String bearerType);
    Map<String, Object> updateSplitPayment(String splitCode, Map<String, Object> updateData);

    // Transfer operations
    Map<String, Object> initiateTransfer(String source, String reason, BigDecimal amount, String recipient);
    Map<String, Object> finalizeTransfer(String transferCode, String otp);

    // Bank operations
    Map<String, Object> listBanks();
    Map<String, Object> resolveAccountNumber(String accountNumber, String bankCode);

    // Webhook verification
    boolean verifyWebhookSignature(String payload, String signature);
}