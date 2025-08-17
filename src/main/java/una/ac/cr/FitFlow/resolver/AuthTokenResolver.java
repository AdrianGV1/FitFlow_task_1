package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

import una.ac.cr.FitFlow.dto.AuthTokenDTO;
import una.ac.cr.FitFlow.service.AuthToken.AuthTokenService;

@RequiredArgsConstructor
@Controller
@Validated
public class AuthTokenResolver {
    private final AuthTokenService authTokenService;

    @QueryMapping
    public AuthTokenDTO getAuthTokenById(@Argument String tokenId) {
        return authTokenService.findByToken(tokenId);
    }

    @QueryMapping
    public Page<AuthTokenDTO> listByUserId(@Argument int page, @Argument int size, @Argument Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        return authTokenService.listByUserId(userId, pageable);
    }

    @MutationMapping
    public AuthTokenDTO createAuthToken(@Valid @Argument AuthTokenDTO authTokenDTO) {
        return authTokenService.create(authTokenDTO);
    }

    @MutationMapping
    public Boolean deleteAuthToken(@Argument String tokenId) {
        if (!authTokenService.isValid(tokenId)) {
            return false;
        }
        authTokenService.delete(tokenId);
        return true;
    }
}
