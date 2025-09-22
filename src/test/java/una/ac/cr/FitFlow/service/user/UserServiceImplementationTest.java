package una.ac.cr.FitFlow.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import una.ac.cr.FitFlow.dto.User.LoginTokenDTO;
import una.ac.cr.FitFlow.mapper.MapperForUser;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.RoleRepository;
import una.ac.cr.FitFlow.repository.UserRepository;
import una.ac.cr.FitFlow.security.JwtService;
import una.ac.cr.FitFlow.security.PasswordHashService;
import una.ac.cr.FitFlow.security.PasswordPolicy;

@ExtendWith(MockitoExtension.class)
class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordHashService passwordHashService;

    @Mock
    private PasswordPolicy passwordPolicy;

    private MapperForUser mapper;

    private UserServiceImplementation service;

    @BeforeEach
    void setUp() {
        mapper = new MapperForUser();
        service = new UserServiceImplementation(
                userRepository,
                roleRepository,
                habitRepository,
                jwtService,
                mapper,
                passwordHashService,
                passwordPolicy
        );
    }

    @Test
    void loginByMailReturnsJwtTokenWithExpiryFromJwtService() {
        Role role = Role.builder()
                .id(1L)
                .module(Role.Module.GUIAS)
                .permission(Role.Permission.EDITOR)
                .build();

        User user = User.builder()
                .id(42L)
                .email("user@example.com")
                .password("hashed")
                .roles(Set.of(role))
                .build();

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordHashService.matches("secret", "hashed")).thenReturn(true);

        Set<String> expectedRoles = Set.of("GUIAS_EDITOR");
        when(jwtService.generateToken("user@example.com", expectedRoles)).thenReturn("jwt-token");
        when(jwtService.getValidityMillis()).thenReturn(60_000L);

        OffsetDateTime beforeCall = OffsetDateTime.now(ZoneOffset.UTC);
        LoginTokenDTO result = service.loginByMail("user@example.com", "secret");
        OffsetDateTime afterCall = OffsetDateTime.now(ZoneOffset.UTC);

        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getUserId()).isEqualTo(42L);

        OffsetDateTime expectedLower = beforeCall.plusSeconds(60_000L / 1000);
        OffsetDateTime expectedUpper = afterCall.plusSeconds(60_000L / 1000);
        assertThat(result.getExpiresAt())
                .isAfterOrEqualTo(expectedLower.minusSeconds(1))
                .isBeforeOrEqualTo(expectedUpper.plusSeconds(1));

        verify(jwtService).generateToken("user@example.com", expectedRoles);
        verify(jwtService).getValidityMillis();
    }

    @Test
    void loginByMailRejectsInvalidPassword() {
        User user = User.builder()
                .id(42L)
                .email("user@example.com")
                .password("hashed")
                .build();

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordHashService.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> service.loginByMail("user@example.com", "wrong"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Clave incorrecta");
    }
}
