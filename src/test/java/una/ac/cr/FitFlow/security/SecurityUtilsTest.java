package una.ac.cr.FitFlow.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import una.ac.cr.FitFlow.model.Role;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setAuth(String principalEmail, String... authorityNames) {
        Set<SimpleGrantedAuthority> auths = java.util.Arrays.stream(authorityNames)
                .map(SimpleGrantedAuthority::new)
                .collect(java.util.stream.Collectors.toSet());
        Authentication a = new UsernamePasswordAuthenticationToken(principalEmail, null, auths);
        SecurityContextHolder.getContext().setAuthentication(a);
    }

    @Test
    void currentAuthOrThrow_throwsWhenNoAuthentication() {
        assertThatThrownBy(SecurityUtils::currentAuthOrThrow)
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void currentAuthOrThrow_throwsWhenAuthoritiesAreNull() {
        // Creamos un Authentication mock que devuelva authorities = null
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getAuthorities()).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(mockAuth);

        assertThatThrownBy(SecurityUtils::currentAuthOrThrow)
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void currentEmail_returnsPrincipalString() {
        setAuth("user@example.com", "GUIAS_EDITOR");
        assertThat(SecurityUtils.currentEmail()).isEqualTo("user@example.com");
    }

    @Test
    void currentAuthorityNames_collectsAuthorityStrings() {
        setAuth("user@example.com", "GUIAS_EDITOR", "PROGRESO_AUDITOR");

        Set<String> names = SecurityUtils.currentAuthorityNames();
        assertThat(names).containsExactlyInAnyOrder("GUIAS_EDITOR", "PROGRESO_AUDITOR");
    }

    @Test
    void authority_buildsModuleUnderscorePermission() {
        String s = SecurityUtils.authority(Role.Module.GUIAS, Role.Permission.EDITOR);
        assertThat(s).isEqualTo("GUIAS_EDITOR");
    }

    @Test
    void has_hasAny_canRead_canWrite_workAsExpected() {
        // El usuario solo tiene GUIAS_EDITOR
        setAuth("user@example.com", "GUIAS_EDITOR");

        assertThat(SecurityUtils.has(Role.Module.GUIAS, Role.Permission.EDITOR)).isTrue();
        assertThat(SecurityUtils.has(Role.Module.GUIAS, Role.Permission.AUDITOR)).isFalse();

        assertThat(SecurityUtils.hasAny(Role.Module.GUIAS, Role.Permission.AUDITOR, Role.Permission.EDITOR)).isTrue();
        assertThat(SecurityUtils.hasAny(Role.Module.PROGRESO, Role.Permission.EDITOR, Role.Permission.AUDITOR)).isFalse();

        // Lectura = EDITOR o AUDITOR del módulo GUIAS
        assertThat(SecurityUtils.canRead(Role.Module.GUIAS)).isTrue();
        // Escritura = solo EDITOR
        assertThat(SecurityUtils.canWrite(Role.Module.GUIAS)).isTrue();

        // En otro módulo no tiene permisos
        assertThat(SecurityUtils.canRead(Role.Module.PROGRESO)).isFalse();
        assertThat(SecurityUtils.canWrite(Role.Module.PROGRESO)).isFalse();
    }

    @Test
    void require_and_requireAny_and_requireRead_and_requireWrite_throwOrNot() {
        // Solo PROGRESO_AUDITOR
        setAuth("user@example.com", "PROGRESO_AUDITOR");

        // OK: canRead(PROGRESO) = true (AUDITOR o EDITOR)
        SecurityUtils.requireRead(Role.Module.PROGRESO);

        // Falla: canWrite(PROGRESO) requiere EDITOR
        assertThatThrownBy(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Forbidden");

        // Falla: require exacto GUIAS_EDITOR sin tenerlo
        assertThatThrownBy(() -> SecurityUtils.require(Role.Module.GUIAS, Role.Permission.EDITOR))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Forbidden");

        // Falla: requireAny en GUIAS sin ninguno
        assertThatThrownBy(() -> SecurityUtils.requireAny(Role.Module.GUIAS, Role.Permission.EDITOR, Role.Permission.AUDITOR))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Forbidden");

        // OK: exacto PROGRESO_AUDITOR
        SecurityUtils.require(Role.Module.PROGRESO, Role.Permission.AUDITOR);
    }

    @Test
    void emptyAuthorities_behaviour() {
        // Auth con 0 authorities (no es null, así que no lanza en currentAuthOrThrow)
        Authentication a = new UsernamePasswordAuthenticationToken("u@x.com", null, java.util.Set.of());
        SecurityContextHolder.getContext().setAuthentication(a);

        // currentAuthOrThrow NO lanza
        Authentication got = SecurityUtils.currentAuthOrThrow();
        assertThat(got).isNotNull();

        // currentEmail devuelve principal como String
        assertThat(SecurityUtils.currentEmail()).isEqualTo("u@x.com");

        // No tiene permisos
        assertThat(SecurityUtils.currentAuthorityNames()).isEmpty();
        assertThat(SecurityUtils.canRead(Role.Module.GUIAS)).isFalse();
        assertThat(SecurityUtils.canWrite(Role.Module.GUIAS)).isFalse();
        assertThatThrownBy(() -> SecurityUtils.requireRead(Role.Module.GUIAS))
                .isInstanceOf(AccessDeniedException.class);
    }
}
