package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.models.ClientProfile;
import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Balance;
import com.stripe.model.PaymentIntent;

import java.math.BigDecimal;

public interface StripeService {
    String createConnectAccount(ServiceProviderProfile provider) throws StripeException;

    /**
     * Create onboarding link for provider to complete account setup
     */
    String createOnboardingLink(String accountId, String refreshUrl, String returnUrl) throws StripeException;

    /**
     * Create Stripe customer for client
     */
    String createCustomer(ClientProfile client) throws StripeException;

    /**
     * Create payment intent with platform fee
     */
    PaymentIntent createPaymentIntent(Long clientId, Long providerId,
                                      BigDecimal amount, String description) throws StripeException;

    /**
     * Get account balance (collected platform fees)
     */
    Balance getAccountBalance() throws StripeException;

    /**
     * Get Connect account details
     */
    Account getConnectAccount(String accountId) throws StripeException;

    /**
     * Check if provider has completed onboarding
     */
    boolean isProviderOnboardingComplete(String accountId) throws StripeException;

    /**
     * Calculate platform fee based on amount
     */
    BigDecimal calculatePlatformFee(BigDecimal amount);

    /**
     * Refund a payment
     */
    void refundPayment(String paymentIntentId, BigDecimal amount) throws StripeException;
}
