package HireCraft.com.SpringBoot.dtos.response;

import HireCraft.com.SpringBoot.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long clientId;
    private Long providerId;
    private Long bookingId;
    private BigDecimal totalAmount;
    private BigDecimal platformFee;
    private BigDecimal providerAmount;
    private PaymentStatus status;
    private String paymentMethod;
    private String externalTransactionId;
    private String description;
    private LocalDateTime createdAt;
}
