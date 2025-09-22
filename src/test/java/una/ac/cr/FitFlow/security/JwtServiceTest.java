package una.ac.cr.FitFlow.security;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void shouldGenerateAndParseTokenWithDefaultSecret() {
        JwtService service = new JwtService();

        String token = assertDoesNotThrow(
            () -> service.generateToken("user@example.com", Set.of("USER")));

        assertNotNull(token);
        assertTrue(service.validate(token));
        assertEquals("user@example.com", service.getEmailFromToken(token));
        assertEquals("user@example.com", service.getUserNameFromToken(token));
        assertEquals(Set.of("USER"), service.getRolesFromToken(token));
    }
}
