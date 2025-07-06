package HireCraft.com.SpringBoot.models;

import HireCraft.com.SpringBoot.enums.EscrowStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "escrow_payments")
@Data
@NoArgsConstructor
public class EscrowPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private PaymentTransaction transaction;

    @Column(name = "escrow_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal escrowAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscrowStatus status;

    @Column(name = "held_at")
    private LocalDateTime heldAt;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @Column(name = "release_reason")
    private String releaseReason;

    @Column(name = "released_by")
    private String releasedBy; // CLIENT, PROVIDER, ADMIN

    @Column(name = "dispute_reason")
    private String disputeReason;

    @Column(name = "auto_release_date")
    private LocalDateTime autoReleaseDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        heldAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
