package HireCraft.com.SpringBoot.dtos.requests;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubaccountCreationRequest {
    private Long providerId;
    private String businessName;
    private String settlementBank;
    private String accountNumber;
    private BigDecimal percentageCharge;
}
