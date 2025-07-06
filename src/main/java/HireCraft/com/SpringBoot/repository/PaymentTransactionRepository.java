package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.PaymentTransaction;
import HireCraft.com.SpringBoot.enums.TransactionStatus;
import HireCraft.com.SpringBoot.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByReference(String reference);

    Optional<PaymentTransaction> findByPaystackReference(String paystackReference);

    List<PaymentTransaction> findByClientIdAndStatus(Long clientId, TransactionStatus status);

    List<PaymentTransaction> findByProviderIdAndStatus(Long providerId, TransactionStatus status);

    List<PaymentTransaction> findByBookingId(Long bookingId);

    List<PaymentTransaction> findByStatusAndType(TransactionStatus status, TransactionType type);

    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.client.id = :clientId AND pt.createdAt BETWEEN :startDate AND :endDate")
    List<PaymentTransaction> findByClientIdAndDateRange(@Param("clientId") Long clientId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.provider.id = :providerId AND pt.createdAt BETWEEN :startDate AND :endDate")
    List<PaymentTransaction> findByProviderIdAndDateRange(@Param("providerId") Long providerId,
                                                          @Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.provider.id = :providerId AND pt.status = 'SUCCESS'")
    BigDecimal getTotalEarningsByProvider(@Param("providerId") Long providerId);

    @Query("SELECT SUM(pt.platformFee) FROM PaymentTransaction pt WHERE pt.status = 'SUCCESS' AND pt.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalPlatformFeeByDateRange(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
}
