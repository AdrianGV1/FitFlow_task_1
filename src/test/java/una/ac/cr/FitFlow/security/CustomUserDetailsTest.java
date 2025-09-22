package una.ac.cr.FitFlow.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.model.User;

class CustomUserDetailsTest {

    @Test
    void authorities_areBuiltFromRoles_asModuleUnderscorePermission_andAreUnmodifiable() {
        Role r1 = Role.builder()
                .id(1L)
                .module(Role.Module.GUIAS)
                .permission(Role.Permission.EDITOR)
                .build();
        Role r2 = Role.builder()
                .id(2L)
                .module(Role.Module.PROGRESO)
                .permission(Role.Permission.AUDITOR)
                .build();

        User user = User.builder()
                .id(42L)
                .email("user@example.com")
                .password("hashed")
                .roles(new HashSet<>(Arrays.asList(r1, r2)))
                .build();

        CustomUserDetails cud = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> auths = cud.getAuthorities();

        // Comparamos por el nombre de la autoridad para evitar problemas de genéricos
        assertThat(auths)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("GUIAS_EDITOR", "PROGRESO_AUDITOR");

        // Intento de mutar debe fallar (toUnmodifiableSet())
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Collection raw = (Collection) auths;
        assertThatThrownBy(() -> raw.add(new SimpleGrantedAuthority("X_Y")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void authorities_emptyWhenRolesNull_andAreUnmodifiable() {
        User user = User.builder()
                .id(7L)
                .email("x@y.com")
                .password("p")
                .build();
        user.setRoles(null); // forzamos el caso roles == null

        CustomUserDetails cud = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> auths = cud.getAuthorities();

        assertThat(auths).isEmpty();

        // También debe ser inmutable (Set.of())
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Collection raw = (Collection) auths;
        assertThatThrownBy(() -> raw.add(new SimpleGrantedAuthority("ANY")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void usernamePasswordAndFlags_areCorrect() {
        User user = User.builder()
                .id(99L)
                .email("alice@example.com")
                .password("secret")
                .build();

        CustomUserDetails cud = new CustomUserDetails(user);

        assertThat(cud.getUsername()).isEqualTo("alice@example.com");
        assertThat(cud.getPassword()).isEqualTo("secret");
        assertThat(cud.isAccountNonExpired()).isTrue();
        assertThat(cud.isAccountNonLocked()).isTrue();
        assertThat(cud.isCredentialsNonExpired()).isTrue();
        assertThat(cud.isEnabled()).isTrue();
    }

    @Test
    void domainAccessors_exposeUserData() {
        User user = User.builder()
                .id(123L)
                .email("bob@example.com")
                .password("p")
                .build();

        CustomUserDetails cud = new CustomUserDetails(user);

        assertThat(cud.getId()).isEqualTo(123L);
        assertThat(cud.getEmail()).isEqualTo("bob@example.com");
        assertThat(cud.getDomainUser()).isSameAs(user);
    }
}
