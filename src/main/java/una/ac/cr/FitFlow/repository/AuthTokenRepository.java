package una.ac.cr.FitFlow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import graphql.com.google.common.base.Optional;
import una.ac.cr.FitFlow.model.AuthToken;

public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {
    Optional<AuthToken> findByToken(String token);
    boolean existsByToken(String token);
    void deleteByToken(String token);
} 
