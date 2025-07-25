package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByBookingIdOrderBySentAtAsc(Long bookingId);
}
