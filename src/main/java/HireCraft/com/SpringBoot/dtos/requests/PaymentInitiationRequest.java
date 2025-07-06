package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationRequest {
    private Long bookingId;
    private BigDecimal amount;
    private String email;
    private String callbackUrl;
    private String currency = "NGN";
    private boolean useEscrow = false;

    public Long getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getCurrency() {
        return currency;
    }

    public boolean isUseEscrow() {
        return useEscrow;
    }
}
