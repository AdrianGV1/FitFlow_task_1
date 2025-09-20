package una.ac.cr.FitFlow.repository;

import java.util.List;
import java.util.Optional;

import una.ac.cr.FitFlow.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByEmailContainingIgnoreCase(String email);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}
