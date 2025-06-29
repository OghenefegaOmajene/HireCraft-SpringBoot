package HireCraft.com.SpringBoot.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentBreakdown {
    private BigDecimal totalAmount;
    private BigDecimal platformFeePercentage;
    private BigDecimal platformFee;
    private BigDecimal providerAmount;

    public static PaymentBreakdown calculate(BigDecimal amount, BigDecimal feePercentage) {
        BigDecimal platformFee = amount.multiply(feePercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal providerAmount = amount.subtract(platformFee);

        return PaymentBreakdown.builder()
                .totalAmount(amount)
                .platformFeePercentage(feePercentage)
                .platformFee(platformFee)
                .providerAmount(providerAmount)
                .build();
    }
}
