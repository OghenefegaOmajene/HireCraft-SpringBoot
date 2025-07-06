package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.EscrowPayment;
import HireCraft.com.SpringBoot.enums.EscrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EscrowPaymentRepository extends JpaRepository<EscrowPayment, Long> {

    Optional<EscrowPayment> findByTransactionId(Long transactionId);

    List<EscrowPayment> findByStatus(EscrowStatus status);

    List<EscrowPayment> findByTransactionClientId(Long clientId);

    List<EscrowPayment> findByTransactionProviderId(Long providerId);

    @Query("SELECT ep FROM EscrowPayment ep WHERE ep.status = 'HELD' AND ep.autoReleaseDate <= :currentDate")
    List<EscrowPayment> findEscrowsReadyForAutoRelease(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT ep FROM EscrowPayment ep WHERE ep.transaction.booking.id = :bookingId")
    Optional<EscrowPayment> findByBookingId(@Param("bookingId") Long bookingId);
}
