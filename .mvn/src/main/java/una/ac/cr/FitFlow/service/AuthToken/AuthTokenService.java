package una.ac.cr.FitFlow.service.AuthToken;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenInputDTO;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenOutputDTO;

public interface AuthTokenService {
  AuthTokenOutputDTO create(AuthTokenInputDTO dto);
  AuthTokenOutputDTO findByToken(String token);
  boolean isValid(String token);
  void delete(String token);
  long purgeExpired();
  Page<AuthTokenOutputDTO> listByUserId(Long userId, Pageable pageable);
}
