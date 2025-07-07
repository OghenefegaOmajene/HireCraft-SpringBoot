package HireCraft.com.SpringBoot.dtos.response;

import HireCraft.com.SpringBoot.enums.TransactionStatus;
import HireCraft.com.SpringBoot.enums.TransactionType;
import HireCraft.com.SpringBoot.models.PaymentTransaction;
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
public class PaymentTransactionResponse {
    private Long id;
    private String reference;
    private String paystackReference;
    private BigDecimal amount;
    private String currency;
    private TransactionStatus status;
    private TransactionType type;
    private String description;
    private Long clientId;
    private String clientName;
    private Long providerId;
    private String providerName;
    private Long bookingId;
    private BigDecimal platformFee;
    private BigDecimal providerAmount;
    private String paymentMethod;
    private String paystackTransactionId;
    private String gatewayResponse;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentTransactionResponse fromEntity(PaymentTransaction transaction) {
        return PaymentTransactionResponse.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .paystackReference(transaction.getPaystackReference())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .clientId(transaction.getClient() != null ? transaction.getClient().getId() : null)
                .clientName(transaction.getClient() != null ?
                        transaction.getClient().getUser().getFirstName() + " " + transaction.getClient().getUser().getLastName() : null)
                .providerId(transaction.getProvider() != null ? transaction.getProvider().getId() : null)
                .providerName(transaction.getProvider() != null ?
                        transaction.getProvider().getUser().getFirstName() + " " + transaction.getProvider().getUser().getLastName() : null)
                .bookingId(transaction.getBooking() != null ? transaction.getBooking().getId() : null)
                .platformFee(transaction.getPlatformFee())
                .providerAmount(transaction.getProviderAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .paystackTransactionId(transaction.getPaystackTransactionId())
                .gatewayResponse(transaction.getGatewayResponse())
                .paidAt(transaction.getPaidAt())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    // Safe method that handles null entities
    public static PaymentTransactionResponse fromEntitySafe(PaymentTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        try {
            return fromEntity(transaction);
        } catch (Exception e) {
            // Fallback with minimal data if relationships fail
            return PaymentTransactionResponse.builder()
                    .id(transaction.getId())
                    .reference(transaction.getReference())
                    .amount(transaction.getAmount())
                    .currency(transaction.getCurrency())
                    .status(transaction.getStatus())
                    .type(transaction.getType())
                    .description(transaction.getDescription())
                    .platformFee(transaction.getPlatformFee())
                    .providerAmount(transaction.getProviderAmount())
                    .createdAt(transaction.getCreatedAt())
                    .build();
        }
    }
}
