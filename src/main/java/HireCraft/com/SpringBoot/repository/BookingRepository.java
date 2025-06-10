package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByProviderProfile_Id(Long providerId); // ✅ CORRECT
}
