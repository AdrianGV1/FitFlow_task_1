package una.ac.cr.FitFlow.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletResponse;

class SecurityHandlersConfigTest {

    private final SecurityHandlersConfig config = new SecurityHandlersConfig();

    @Test
    void authenticationEntryPoint_writes401Json() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        var entryPoint = config.authenticationEntryPoint();
        entryPoint.commence(request, response, new AuthenticationException("x") {});

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo("{\"error\":\"unauthorized\"}");
    }

    @Test
    void accessDeniedHandler_writes403Json() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        var handler = config.accessDeniedHandler();
        handler.handle(request, response, new AccessDeniedException("nope"));

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo("{\"error\":\"forbidden\"}");
    }
}
