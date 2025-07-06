package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.SplitPayment;
import HireCraft.com.SpringBoot.enums.SplitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SplitPaymentRepository extends JpaRepository<SplitPayment, Long> {

    Optional<SplitPayment> findByTransactionId(Long transactionId);

    Optional<SplitPayment> findBySplitCode(String splitCode);

    List<SplitPayment> findByStatus(SplitStatus status);

    List<SplitPayment> findByTransactionProviderId(Long providerId);
}
