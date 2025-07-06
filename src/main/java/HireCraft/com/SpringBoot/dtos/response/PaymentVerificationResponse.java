package HireCraft.com.SpringBoot.dtos.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
public class PaymentVerificationResponse {
    private boolean success;
    private String reference;
    private BigDecimal amount;
    private String status;
    private String gatewayResponse;
    private LocalDateTime paidAt;
    private String message;

    public PaymentVerificationResponse(boolean success, String reference, BigDecimal amount, String status, String gatewayResponse, LocalDateTime paidAt, String message) {
        this.success = success;
        this.reference = reference;
        this.amount = amount;
        this.status = status;
        this.gatewayResponse = gatewayResponse;
        this.paidAt = paidAt;
        this.message = message;
    }


}