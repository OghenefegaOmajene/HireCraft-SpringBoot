package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.enums.TransactionStatus;
import HireCraft.com.SpringBoot.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByClientProfile_Id(Long clientId);
    List<Transaction> findByProviderProfile_Id(Long providerId);

    Optional<Transaction> findByStripePaymentIntentId(String paymentIntentId);
    List<Transaction> findByStatus(TransactionStatus status);

    @Query("SELECT SUM(t.platformFee) FROM Transaction t WHERE t.status = 'COMPLETED'")
    BigDecimal getTotalPlatformFeesCollected();

    // Fixed: Using the correct field names in custom queries
    @Query("SELECT t FROM Transaction t WHERE t.clientProfile.id = :clientId ORDER BY t.createdAt DESC")
    List<Transaction> findByClientIdOrderByCreatedAtDesc(@Param("clientId") Long clientId);

    @Query("SELECT t FROM Transaction t WHERE t.providerProfile.id = :providerId ORDER BY t.createdAt DESC")
    List<Transaction> findByProviderIdOrderByCreatedAtDesc(@Param("providerId") Long providerId);
}
