package HireCraft.com.SpringBoot.models;

import HireCraft.com.SpringBoot.enums.SplitStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "split_payments")
@Data
@NoArgsConstructor
public class SplitPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private PaymentTransaction transaction;

    @Column(name = "split_code", unique = true)
    private String splitCode;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "platform_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal platformAmount;

    @Column(name = "provider_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal providerAmount;

    @Column(name = "platform_percentage", precision = 5, scale = 2)
    private BigDecimal platformPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}