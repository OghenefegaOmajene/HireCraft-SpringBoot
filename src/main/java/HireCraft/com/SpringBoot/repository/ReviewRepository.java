package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProvider_Id(Long providerId);
    List<Review> findByClient_Id(Long clientId);
    List<Review> findByClient_IdAndProvider_Id(Long clientId, Long providerId);

}
