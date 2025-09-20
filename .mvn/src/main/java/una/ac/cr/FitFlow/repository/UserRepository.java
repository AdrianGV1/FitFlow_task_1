package una.ac.cr.FitFlow.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import una.ac.cr.FitFlow.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByUsernameAndIdNot(String username, Long id);

    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String username, String email, Pageable pageable);

    
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
