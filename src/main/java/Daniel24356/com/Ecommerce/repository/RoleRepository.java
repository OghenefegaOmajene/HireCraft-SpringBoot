package Daniel24356.com.Ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Daniel24356.com.Ecommerce.models.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
