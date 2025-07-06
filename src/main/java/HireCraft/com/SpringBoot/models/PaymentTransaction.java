package HireCraft.com.SpringBoot.models;

import HireCraft.com.SpringBoot.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Payment Transaction Model
@Entity
@Table(name = "payment_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @Column(name = "paystack_reference")
    private String paystackReference;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency = "NGN";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private String description;

    // Client who is making the payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile client;

    // Service provider receiving the payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ServiceProviderProfile provider;

    // Related service booking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    // Platform fee
    @Column(name = "platform_fee", precision = 10, scale = 2)
    private BigDecimal platformFee;

    // Provider's amount after platform fee
    @Column(name = "provider_amount", precision = 10, scale = 2)
    private BigDecimal providerAmount;

    // Payment method used
    @Column(name = "payment_method")
    private String paymentMethod;

    // Paystack transaction data
    @Column(name = "paystack_transaction_id")
    private String paystackTransactionId;

    @Column(name = "authorization_code")
    private String authorizationCode;

    @Column(name = "gateway_response")
    private String gatewayResponse;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

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
