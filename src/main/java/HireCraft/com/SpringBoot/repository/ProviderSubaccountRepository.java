package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.ProviderSubaccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderSubaccountRepository extends JpaRepository<ProviderSubaccount, Long> {

    Optional<ProviderSubaccount> findByProviderId(Long providerId);

    Optional<ProviderSubaccount> findBySubaccountCode(String subaccountCode);

    List<ProviderSubaccount> findByIsActive(Boolean isActive);

    boolean existsByProviderId(Long providerId);
}
