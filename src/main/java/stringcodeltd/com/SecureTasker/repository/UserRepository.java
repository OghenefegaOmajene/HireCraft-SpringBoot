package stringcodeltd.com.SecureTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stringcodeltd.com.SecureTasker.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
