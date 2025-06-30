package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.PaymentRequest;
import HireCraft.com.SpringBoot.dtos.response.PaymentResponse;
import HireCraft.com.SpringBoot.dtos.PaymentBreakdown;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long paymentId);
    List<PaymentResponse> getClientPayments(Long clientId);
    List<PaymentResponse> getProviderPayments(Long providerId);
    PaymentBreakdown calculatePaymentBreakdown(BigDecimal amount);
    BigDecimal calculateProviderEarnings(Long providerId, LocalDateTime startDate, LocalDateTime endDate);
    BigDecimal calculatePlatformRevenue(LocalDateTime startDate, LocalDateTime endDate);
}
