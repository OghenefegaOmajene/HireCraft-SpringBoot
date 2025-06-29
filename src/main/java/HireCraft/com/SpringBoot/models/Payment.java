package HireCraft.com.SpringBoot.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long providerId;

    private Long projectId; // Optional - for project-based payments

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // What client pays

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal platformFeePercentage; // e.g., 8.00 for 8%

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal platformFee; // Calculated fee amount

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal providerAmount; // What provider receives

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private String paymentMethod; // STRIPE, PAYPAL, etc.

    private String externalTransactionId; // From payment processor

    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}