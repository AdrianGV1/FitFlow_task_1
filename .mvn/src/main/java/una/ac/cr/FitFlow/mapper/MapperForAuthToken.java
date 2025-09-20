package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenInputDTO;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenOutputDTO;
import una.ac.cr.FitFlow.model.AuthToken;
import una.ac.cr.FitFlow.model.User;

@Component
public class MapperForAuthToken {

    public AuthTokenOutputDTO toDto(AuthToken t) {
        if (t == null) return null;
        return AuthTokenOutputDTO.builder()
                .token(t.getToken())
                .expiresAt(t.getExpiresAt())
                .userId(t.getUser() != null ? t.getUser().getId() : null)
                .build();
    }

    public AuthToken toEntity(AuthTokenInputDTO in, User user) {
        if (in == null) return null;
        return AuthToken.builder()
                .token(in.getToken())
                .expiresAt(in.getExpiresAt())
                .user(user)
                .build();
    }
}
