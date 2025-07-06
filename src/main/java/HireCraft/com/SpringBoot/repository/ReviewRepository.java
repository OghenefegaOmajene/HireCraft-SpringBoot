package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProviderProfile_IdOrderByCreatedAtDesc(Long providerId);
    List<Review> findByClientProfile_IdOrderByCreatedAtDesc(Long clientId);
    List<Review> findByClientProfile_Id(Long clientId);
    List<Review> findByProviderProfile_Id(Long providerId);
    List<Review> findByClientProfile_IdAndProviderProfile_Id(Long clientId, Long providerId);
    long countByProviderProfile_Id(Long providerId);

    boolean existsByBookingId(Long bookingId);
    Optional<Review> findByBookingId(Long bookingId);

    boolean existsByBookingIdAndClientProfileId(Long bookingId, Long clientProfileId);

    // Method to find reviews by booking IDs (useful for checking multiple bookings)
    List<Review> findByBookingIdIn(List<Long> bookingIds);
}
