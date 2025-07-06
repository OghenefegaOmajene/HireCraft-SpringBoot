package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.OnboardingRequest;
import HireCraft.com.SpringBoot.dtos.requests.PaymentRequest;

import com.stripe.model.Balance;
import java.util.Map;

public interface PaymentService {

    /**
     * Create a Stripe Connect account for a service provider
     * @param request OnboardingRequest containing provider ID and URLs
     * @return Map containing account ID and onboarding URL
     * @throws Exception if provider not found or Stripe operation fails
     */
    Map<String, Object> createConnectAccount(OnboardingRequest request) throws Exception;

    /**
     * Create a payment intent for a transaction
     * @param request PaymentRequest containing client ID, provider ID, amount, and description
     * @return Map containing client secret and payment intent details
     * @throws Exception if client/provider not found or payment intent creation fails
     */
    Map<String, Object> createPaymentIntent(PaymentRequest request) throws Exception;

    /**
     * Get the platform's account balance
     * @return Balance object from Stripe
     * @throws Exception if balance retrieval fails
     */
    Balance getAccountBalance() throws Exception;

    /**
     * Get Connect account details and status
     * @param accountId Stripe Connect account ID
     * @return Map containing account details and status
     * @throws Exception if account retrieval fails
     */
    Map<String, Object> getConnectAccountDetails(String accountId) throws Exception;

    /**
     * Check the onboarding status of a service provider
     * @param providerId Service provider ID
     * @return Map containing onboarding status information
     * @throws Exception if provider not found or status check fails
     */
    Map<String, Object> getProviderOnboardingStatus(Long providerId) throws Exception;
}
//public interface PaymentService {
//
//    PaymentResponse processPayment(PaymentRequest request, UserDetails userDetails);
//    PaymentResponse getPaymentById(Long paymentId, UserDetails userDetails);
//    List<PaymentResponse> getClientPayments(UserDetails userDetails);
//    List<PaymentResponse> getProviderPayments(UserDetails userDetails);
//    List<PaymentResponse> getBookingPayments(Long bookingId, UserDetails userDetails);
//    BigDecimal calculateProviderEarnings(Long providerId, LocalDateTime startDate,
//                                         LocalDateTime endDate, UserDetails userDetails);
//
//    PaymentResponse processPayment(PaymentRequest request);
//    PaymentResponse getPaymentById(Long paymentId);
//    List<PaymentResponse> getClientPayments(Long clientId);
//    List<PaymentResponse> getProviderPayments(Long providerId);
//    List<PaymentResponse> getBookingPayments(Long bookingId);
//    BigDecimal calculateProviderEarnings(Long providerId, LocalDateTime startDate, LocalDateTime endDate);
//
//    PaymentBreakdown calculatePaymentBreakdown(BigDecimal amount);
//    BigDecimal calculatePlatformRevenue(LocalDateTime startDate, LocalDateTime endDate);
//}