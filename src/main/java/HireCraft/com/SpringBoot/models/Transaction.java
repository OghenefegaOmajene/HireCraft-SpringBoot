package HireCraft.com.SpringBoot.models;

import HireCraft.com.SpringBoot.enums.TransactionStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientProfile clientProfile;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ServiceProviderProfile providerProfile;

    private BigDecimal amount;
    private BigDecimal platformFee;
    private BigDecimal providerAmount;

    private String stripePaymentIntentId;
    private String stripeTransferId;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime createdAt;
    private String description;

    public Transaction(Long id, ClientProfile clientProfile, ServiceProviderProfile providerProfile, BigDecimal amount, BigDecimal platformFee, BigDecimal providerAmount, String stripePaymentIntentId, String stripeTransferId, TransactionStatus status, LocalDateTime createdAt, String description) {
        this.id = id;
        this.clientProfile = clientProfile;
        this.providerProfile = providerProfile;
        this.amount = amount;
        this.platformFee = platformFee;
        this.providerAmount = providerAmount;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.stripeTransferId = stripeTransferId;
        this.status = status;
        this.createdAt = createdAt;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClientProfile getClientProfile() {
        return clientProfile;
    }

    public void setClientProfile(ClientProfile clientProfile) {
        this.clientProfile = clientProfile;
    }

    public ServiceProviderProfile getProviderProfile() {
        return providerProfile;
    }

    public void setProviderProfile(ServiceProviderProfile providerProfile) {
        this.providerProfile = providerProfile;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public BigDecimal getProviderAmount() {
        return providerAmount;
    }

    public void setProviderAmount(BigDecimal providerAmount) {
        this.providerAmount = providerAmount;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public String getStripeTransferId() {
        return stripeTransferId;
    }

    public void setStripeTransferId(String stripeTransferId) {
        this.stripeTransferId = stripeTransferId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
