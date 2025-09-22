package una.ac.cr.FitFlow.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private AppUserDetailsService service;

    @BeforeEach
    void setUp() {
        service = new AppUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_normalizesEmail_andReturnsUserDetails() {
        // given
        String raw = "  TEST@Example.COM  ";
        String normalized = "test@example.com";

        User entity = User.builder()
                .id(42L)
                .email("test@example.com")
                .password("hashed-pass")
                .build();

        when(userRepository.findByEmail(normalized))
                .thenReturn(Optional.of(entity));

        // when
        UserDetails ud = service.loadUserByUsername(raw);

        // then
        assertThat(ud).isNotNull();
        assertThat(ud).isInstanceOf(CustomUserDetails.class);
        assertThat(ud.getUsername()).isEqualTo("test@example.com"); // suele mapear al email
        assertThat(ud.getPassword()).isEqualTo("hashed-pass");

        verify(userRepository).findByEmail(normalized);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_throwsWhenUserNotFound() {
        String normalized = "missing@x.com";
        when(userRepository.findByEmail(normalized)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("  missing@x.com "))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail(normalized);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_withNullEmail_throwsUsernameNotFound() {
        // Si alguien llama con null, el servicio normaliza a null y consulta el repo con null.
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername(null))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail(null);
        verifyNoMoreInteractions(userRepository);
    }
}
