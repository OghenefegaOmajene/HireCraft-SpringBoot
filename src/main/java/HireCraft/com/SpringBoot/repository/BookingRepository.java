package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.enums.BookingStatus;
import HireCraft.com.SpringBoot.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByProviderProfile_Id(Long providerId); // ✅ CORRECT
    List<Booking> findByClientProfile_Id(Long clientId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.providerProfile.id = :providerId AND b.createdAt >= :startDate AND b.createdAt < :endDate AND b.status = 'PENDING'")
    long countNewBookingsForProviderByDate(Long providerId, LocalDateTime startDate, LocalDateTime endDate);

    // New method to count completed jobs for a provider
//    @Query("SELECT COUNT(b) FROM Booking b WHERE b.providerProfile.id = :providerId AND b.status = 'COMPLETED'")
//    long countCompletedJobsForProvider(Long providerId);


    @Query("SELECT COUNT(b) FROM Booking b WHERE b.providerProfile.id = :providerId AND b.status = 'COMPLETED'")
    long countCompletedJobsForProvider(@Param("providerId") Long providerId);

    // --- NEW METHOD FOR COMPLETION RATE ---
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.providerProfile.id = :providerId AND b.status = 'ACCEPTED'")
    long countAcceptedJobsForProvider(@Param("providerId") Long providerId);
    // You might also want to get all pending bookings for display if needed
    List<Booking> findByProviderProfile_IdAndStatus(Long providerId, BookingStatus status);


}

