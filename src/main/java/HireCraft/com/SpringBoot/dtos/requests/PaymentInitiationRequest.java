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
}
