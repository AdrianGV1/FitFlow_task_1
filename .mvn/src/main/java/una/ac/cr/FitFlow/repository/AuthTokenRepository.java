package una.ac.cr.FitFlow.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import una.ac.cr.FitFlow.model.AuthToken;

public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {

    Optional<AuthToken> findByToken(String token);

    boolean existsByToken(String token);

    void deleteByToken(String token);

    Page<AuthToken> findByUser_Id(Long userId, Pageable pageable);

    long deleteByExpiresAtBefore(LocalDateTime dateTime);
}
