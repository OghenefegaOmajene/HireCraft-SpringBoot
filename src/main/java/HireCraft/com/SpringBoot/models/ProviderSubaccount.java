package HireCraft.com.SpringBoot.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "provider_subaccounts")
@Data
@NoArgsConstructor
public class ProviderSubaccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ServiceProviderProfile provider;

    @Column(name = "subaccount_code", unique = true, nullable = false)
    private String subaccountCode;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "settlement_bank", nullable = false)
    private String settlementBank;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "percentage_charge", precision = 5, scale = 2)
    private BigDecimal percentageCharge;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setProvider(ServiceProviderProfile provider) {
        this.provider = provider;
    }

    public void setSubaccountCode(String subaccountCode) {
        this.subaccountCode = subaccountCode;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setSettlementBank(String settlementBank) {
        this.settlementBank = settlementBank;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setPercentageCharge(BigDecimal percentageCharge) {
        this.percentageCharge = percentageCharge;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
