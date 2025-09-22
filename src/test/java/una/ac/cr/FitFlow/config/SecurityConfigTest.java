package una.ac.cr.FitFlow.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import una.ac.cr.FitFlow.security.JwtFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(
    controllers = {},
    excludeFilters = @Filter(
        type = FilterType.REGEX,
        pattern = "una\\.ac\\.cr\\.FitFlow\\.resolver\\..*" // 👈 evita cargar resolvers @Controller
    )
)
@Import(SecurityConfig.class) // 👈 solo tu config de seguridad
class SecurityConfigTest {

    @Autowired MockMvc mvc;

    // Mocks para los beans que SecurityConfig necesita
    @MockBean JwtFilter jwtFilter;
    @MockBean AuthenticationEntryPoint authenticationEntryPoint;
    @MockBean AccessDeniedHandler accessDeniedHandler;

    @BeforeEach
    void passThroughJwtFilter() throws Exception {
        // el filtro no bloquea nada en el test
        doAnswer(inv -> {
            HttpServletRequest req  = inv.getArgument(0);
            HttpServletResponse res = inv.getArgument(1);
            FilterChain chain       = inv.getArgument(2);
            chain.doFilter(req, res);
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());
    }

    @Test
    void graphql_is_permitted_for_post_without_auth() throws Exception {
        var res = mvc.perform(
            post("/graphql")
                .contentType("application/json")
                .content("{\"query\":\"{__typename}\"}")
        ).andReturn();
        // Lo importante: no 401/403
        assertThat(res.getResponse().getStatus()).isNotIn(401, 403);
    }

    @Test
    void options_preflight_is_permitted() throws Exception {
        var res = mvc.perform(options("/graphql")).andReturn();
        assertThat(res.getResponse().getStatus()).isNotIn(401, 403);
    }

    @Test
    void graphiql_is_permitted() throws Exception {
        var res = mvc.perform(get("/graphiql")).andReturn();
        // Puede ser 404 si no tienes la UI; solo valida que no fue bloqueado por seguridad
        assertThat(res.getResponse().getStatus()).isNotIn(401, 403);
    }
}
