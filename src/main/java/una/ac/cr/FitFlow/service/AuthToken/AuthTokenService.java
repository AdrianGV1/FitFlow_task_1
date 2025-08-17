package una.ac.cr.FitFlow.service.AuthToken;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.AuthTokenDTO;

public interface AuthTokenService {
  AuthTokenDTO create(AuthTokenDTO dto);

  AuthTokenDTO findByToken(String token);

  boolean isValid(String token);

  void delete(String token);

  long purgeExpired();

  Page<AuthTokenDTO> listByUserId(Long userId, Pageable pageable);
}
