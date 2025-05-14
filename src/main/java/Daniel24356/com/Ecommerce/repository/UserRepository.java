package Daniel24356.com.Ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Daniel24356.com.Ecommerce.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
