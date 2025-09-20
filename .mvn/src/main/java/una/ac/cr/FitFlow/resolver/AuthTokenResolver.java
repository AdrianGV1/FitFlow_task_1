package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;


import una.ac.cr.FitFlow.dto.AuthTokenPageDTO;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenOutputDTO;
import una.ac.cr.FitFlow.service.AuthToken.AuthTokenService;
import una.ac.cr.FitFlow.service.user.UserService;
import una.ac.cr.FitFlow.dto.AuthTokenDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;    

@RequiredArgsConstructor
@Controller
@Validated
public class AuthTokenResolver {
    private final AuthTokenService authTokenService;
    private final UserService userService;

    @QueryMapping(name = "authTokenById")
    public AuthTokenDTO authTokenById(@Argument("tokenId") String tokenId) {
        return authTokenService.findByToken(tokenId);
    }

    @QueryMapping(name = "authTokensByUserId")
    public AuthTokenPageDTO authTokensByUserId(@Argument("userId") Long userId,
            @Argument("page") int page, @Argument("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuthTokenDTO> pageResult = authTokenService.listByUserId(userId, pageable);
        return AuthTokenPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @MutationMapping(name = "createAuthToken")
    public AuthTokenDTO createAuthToken(@Valid @Argument("input") AuthTokenDTO authTokenDTO) {
        return authTokenService.create(authTokenDTO);
    }

    @MutationMapping(name = "deleteAuthToken")
    public Boolean deleteAuthToken(@Argument("tokenId") String tokenId) {
        if (!authTokenService.isValid(tokenId)) {
            return false;
        }
        authTokenService.delete(tokenId);
        return true;
    }

    @SchemaMapping(typeName = "AuthToken")
    public UserOutputDTO user(Object token) {
        Long userId = null;
        if (token instanceof AuthTokenDTO authTokenDTO) {
            userId = authTokenDTO.getUserId();
        } else if (token instanceof AuthTokenOutputDTO authTokenOutputDTO) {
            userId = authTokenOutputDTO.getUserId();
        }
        return userId != null ? userService.findUserById(userId) : null;
    }
}

