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

    public Long getProviderId() {
        return providerId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getSettlementBank() {
        return settlementBank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getPercentageCharge() {
        return percentageCharge;
    }
}
