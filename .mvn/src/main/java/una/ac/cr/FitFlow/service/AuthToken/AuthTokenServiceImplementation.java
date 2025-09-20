package una.ac.cr.FitFlow.service.AuthToken;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenInputDTO;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForAuthToken;
import una.ac.cr.FitFlow.model.AuthToken;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.AuthTokenRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthTokenServiceImplementation implements AuthTokenService {

    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;
    private final MapperForAuthToken mapper;

    @Override
    @Transactional
    public AuthTokenOutputDTO create(AuthTokenInputDTO dto) {
        if (authTokenRepository.existsByToken(dto.getToken())) {
            throw new IllegalArgumentException("El token ya existe.");
        }
        if (dto.getExpiresAt() == null || !dto.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de expiración debe ser futura.");
        }
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));

        AuthToken saved = authTokenRepository.save(mapper.toEntity(dto, user));
        return mapper.toDto(saved);
    }

    @Override
    public AuthTokenOutputDTO findByToken(String token) {
        AuthToken found = authTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token no encontrado."));
        return mapper.toDto(found);
    }

    @Override
    public boolean isValid(String token) {
        return authTokenRepository.findById(token)
                .map(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Override
    @Transactional
    public void delete(String token) {
        if (!authTokenRepository.existsByToken(token)) {
            throw new IllegalArgumentException("Token no encontrado.");
        }
        authTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public long purgeExpired() {
        return authTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Override
    public Page<AuthTokenOutputDTO> listByUserId(Long userId, org.springframework.data.domain.Pageable pageable) {
        return authTokenRepository.findByUser_Id(userId, pageable).map(mapper::toDto);
    }
}
