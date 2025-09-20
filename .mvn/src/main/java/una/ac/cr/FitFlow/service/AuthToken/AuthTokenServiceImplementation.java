package una.ac.cr.FitFlow.service.AuthToken;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import una.ac.cr.FitFlow.dto.AuthTokenDTO;
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

    private AuthTokenDTO toDto(AuthToken t) {
        return AuthTokenDTO.builder()
                .token(t.getToken())
                .expiresAt(t.getExpiresAt())
                .userId(t.getUser().getId())
                .build();
    }

    private AuthToken toEntity(AuthTokenDTO d) {
        User user = userRepository.findById(d.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + d.getUserId()));
        return AuthToken.builder()
                .token(d.getToken())
                .expiresAt(d.getExpiresAt())
                .user(user)
                .build();
    }

    @Override
    @Transactional
    public AuthTokenDTO create(AuthTokenDTO dto) {
        if (authTokenRepository.existsByToken(dto.getToken())) {
            throw new IllegalArgumentException("El token ya existe");
        }
        if (dto.getExpiresAt() == null || !dto.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de expiración debe ser futura");
        }
        AuthToken saved = authTokenRepository.save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    public AuthTokenDTO findByToken(String token) {
        AuthToken existAuthToken = authTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token no encontrado"));
        return toDto(existAuthToken);
    }

    @Override
    public boolean isValid(String token) {
        return authTokenRepository.findById(token)
                .map(t -> t.getExpiresAt().isAfter(LocalDateTime.now())).orElse(false);
    }

    @Override
    @Transactional
    public void delete(String token) {
        if (!authTokenRepository.existsByToken(token)) {
            throw new IllegalArgumentException("Token no encontrado");
        }
        authTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public long purgeExpired() {
        return authTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Override
    public Page<AuthTokenDTO> listByUserId(Long userId, Pageable pageable) {
        return authTokenRepository.findByUserId(userId, pageable).map(this::toDto);
    }
}
