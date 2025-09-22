package una.ac.cr.FitFlow.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock private JwtService jwtService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain chain;

    private JwtFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtFilter(jwtService);
        SecurityContextHolder.clearContext();
        when(request.getMethod()).thenReturn("GET"); // default
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void runFilter() throws ServletException, IOException {
        // Llama al OncePerRequestFilter#doFilter (público) que a su vez invoca doFilterInternal
        filter.doFilter(request, response, chain);
    }

    @Test
    void optionsRequest_isBypassed() throws Exception {
        when(request.getMethod()).thenReturn("OPTIONS");

        runFilter();

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void noAuthorizationHeader_bypassesFilter() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        runFilter();

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void invalidPrefix_bypassesFilter() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Token abc"); // no empieza con "Bearer "

        runFilter();

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void invalidToken_bypassesFilter() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad");
        when(jwtService.validate("bad")).thenReturn(false);

        runFilter();

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void validToken_setsAuthentication_whenNoExistingAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer good");
        when(jwtService.validate("good")).thenReturn(true);
        when(jwtService.getUserNameFromToken("good")).thenReturn("user@example.com");
        when(jwtService.getRolesFromToken("good")).thenReturn(Set.of("GUIAS_EDITOR", "PROGRESO_AUDITOR"));

        runFilter();

        verify(chain).doFilter(request, response);
        verify(jwtService).validate("good");
        verify(jwtService).getUserNameFromToken("good");
        verify(jwtService).getRolesFromToken("good");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.isAuthenticated()).isTrue();
        assertThat(auth.getPrincipal()).isEqualTo("user@example.com");

        // Evita problemas de genéricos comparando por el String de cada autoridad
        Collection<? extends GrantedAuthority> authorities = (Collection<? extends GrantedAuthority>) auth.getAuthorities();
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("GUIAS_EDITOR", "PROGRESO_AUDITOR");
    }

    @Test
    void validToken_doesNotOverride_whenExistingAuthenticated() throws Exception {
        // Pre-cargamos un Authentication ya autenticado
        Authentication existing = new UsernamePasswordAuthenticationToken(
                "pre-existing", "N/A",
                Set.of(new SimpleGrantedAuthority("X")));
        SecurityContextHolder.getContext().setAuthentication(existing);

        when(request.getHeader("Authorization")).thenReturn("Bearer good");
        when(jwtService.validate("good")).thenReturn(true);

        runFilter();

        // No debe pedir username/roles si ya está autenticado
        verify(jwtService, never()).getUserNameFromToken(anyString());
        verify(jwtService, never()).getRolesFromToken(anyString());
        verify(chain).doFilter(request, response);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isSameAs(existing); // no lo reemplazó
        assertThat(auth.getPrincipal()).isEqualTo("pre-existing");
    }

    @Test
    void validToken_overrides_whenExistingIsAnonymous() throws Exception {
        Authentication anon = new AnonymousAuthenticationToken(
                "key", "anon", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        SecurityContextHolder.getContext().setAuthentication(anon);

        when(request.getHeader("Authorization")).thenReturn("Bearer good");
        when(jwtService.validate("good")).thenReturn(true);
        when(jwtService.getUserNameFromToken("good")).thenReturn("user@example.com");
        when(jwtService.getRolesFromToken("good")).thenReturn(Set.of("GUIAS_EDITOR"));

        runFilter();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo("user@example.com");
        assertThat(auth.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("GUIAS_EDITOR");
    }

    @Test
    void validToken_overrides_whenExistingIsNotAuthenticated() throws Exception {
        // Token sin authorities => isAuthenticated() = false
        Authentication notAuth = new UsernamePasswordAuthenticationToken("someone", null);
        SecurityContextHolder.getContext().setAuthentication(notAuth);

        when(request.getHeader("Authorization")).thenReturn("Bearer good");
        when(jwtService.validate("good")).thenReturn(true);
        when(jwtService.getUserNameFromToken("good")).thenReturn("user@example.com");
        when(jwtService.getRolesFromToken("good")).thenReturn(Set.of());

        runFilter();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo("user@example.com");
        assertThat(auth.isAuthenticated()).isTrue(); // tiene Set (posible vacío) pero UsernamePasswordAuthenticationToken con authorities marca true
    }
}
