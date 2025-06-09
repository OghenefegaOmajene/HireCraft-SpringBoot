package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
