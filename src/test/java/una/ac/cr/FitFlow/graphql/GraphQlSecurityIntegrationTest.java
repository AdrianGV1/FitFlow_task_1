package una.ac.cr.FitFlow.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import una.ac.cr.FitFlow.dto.Guide.GuideInputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.JwtService;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.CompletedActivity.CompletedActivityService;
import una.ac.cr.FitFlow.service.Guide.GuideService;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogService;
import una.ac.cr.FitFlow.service.Reminder.ReminderService;
import una.ac.cr.FitFlow.service.Routine.RoutineService;
import una.ac.cr.FitFlow.service.RoutineActivity.RoutineActivityService;
import una.ac.cr.FitFlow.service.role.RoleService;
import una.ac.cr.FitFlow.service.user.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
        })
@TestPropertySource(properties = {
        "spring.jpa.defer-datasource-initialization=true"
})
class GraphQlSecurityIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GuideService guideService;

    @MockBean
    private HabitService habitService;

    @MockBean
    private RoutineService routineService;

    @MockBean
    private RoutineActivityService routineActivityService;

    @MockBean
    private ReminderService reminderService;

    @MockBean
    private CompletedActivityService completedActivityService;

    @MockBean
    private ProgressLogService progressLogService;


    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @Test
    void queryWithoutJwtReturns401() {
        String query = "query { guides(page:0, size:1, keyword:\"foo\") { totalElements } }";
        ResponseEntity<String> response = postGraphQl(query, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void queryWithAuditorRoleSucceeds() throws Exception {
        when(guideService.listGuides(eq("foo"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(GuideOutputDTO.builder()
                        .id(1L)
                        .title("Sample")
                        .content("Body")
                        .category("PHYSICAL")
                        .build()), PageRequest.of(0, 1), 1));

        String token = jwtService.generateToken("auditor@example.com", Set.of(SecurityUtils.authority(Role.Module.GUIAS, Role.Permission.AUDITOR)));
        String query = "query { guides(page:0, size:1, keyword:\"foo\") { totalElements } }";

        ResponseEntity<String> response = postGraphQl(query, null, token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.path("errors").isMissingNode() || root.path("errors").isNull()).isTrue();
        assertThat(root.path("data").path("guides").path("totalElements").asInt()).isEqualTo(1);
    }

    @Test
    void mutationWithReadOnlyRoleReturns403() {
        String token = jwtService.generateToken("auditor@example.com", Set.of(SecurityUtils.authority(Role.Module.GUIAS, Role.Permission.AUDITOR)));
        String mutation = "mutation($input: GuideCreateInput!) { createGuide(input: $input) { id } }";
        Map<String, Object> variables = Map.of("input", Map.of(
                "title", "New Guide",
                "content", "Content",
                "category", "PHYSICAL"
        ));

        ResponseEntity<String> response = postGraphQl(mutation, variables, token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void mutationWithEditorRoleSucceeds() throws Exception {
        when(guideService.createGuide(any(GuideInputDTO.class)))
                .thenReturn(GuideOutputDTO.builder()
                        .id(99L)
                        .title("Created")
                        .content("Body")
                        .category("PHYSICAL")
                        .build());

        String token = jwtService.generateToken("editor@example.com", Set.of(SecurityUtils.authority(Role.Module.GUIAS, Role.Permission.EDITOR)));
        String mutation = "mutation($input: GuideCreateInput!) { createGuide(input: $input) { id title } }";
        Map<String, Object> variables = Map.of("input", Map.of(
                "title", "New Guide",
                "content", "Content",
                "category", "PHYSICAL"
        ));

        ResponseEntity<String> response = postGraphQl(mutation, variables, token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.path("errors").isMissingNode() || root.path("errors").isNull()).isTrue();
        assertThat(root.path("data").path("createGuide").path("id").asLong()).isEqualTo(99L);
    }

    private ResponseEntity<String> postGraphQl(String query, Map<String, Object> variables, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        Map<String, Object> payload = (variables == null)
                ? Map.of("query", query)
                : Map.of("query", query, "variables", variables);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        return restTemplate.postForEntity("http://localhost:" + port + "/graphql", entity, String.class);
    }
}

