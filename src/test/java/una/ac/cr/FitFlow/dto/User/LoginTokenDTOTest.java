package una.ac.cr.FitFlow.dto.User;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoginTokenDTOTest {

    @Test
    void builder_createsValidObject() {
        OffsetDateTime expiry = OffsetDateTime.now().plusHours(1);

        LoginTokenDTO dto = LoginTokenDTO.builder()
                .token("abc123")
                .expiresAt(expiry)
                .userId(42L)
                .build();

        assertThat(dto.getToken()).isEqualTo("abc123");
        assertThat(dto.getExpiresAt()).isEqualTo(expiry);
        assertThat(dto.getUserId()).isEqualTo(42L);
    }

    @Test
    void setters_updateFieldsCorrectly() {
        LoginTokenDTO dto = new LoginTokenDTO();
        OffsetDateTime expiry = OffsetDateTime.now().plusDays(1);

        dto.setToken("xyz789");
        dto.setExpiresAt(expiry);
        dto.setUserId(99L);

        assertThat(dto.getToken()).isEqualTo("xyz789");
        assertThat(dto.getExpiresAt()).isEqualTo(expiry);
        assertThat(dto.getUserId()).isEqualTo(99L);
    }

    @Test
    void noArgsConstructor_createsEmptyObject() {
        LoginTokenDTO dto = new LoginTokenDTO();

        assertThat(dto.getToken()).isNull();
        assertThat(dto.getExpiresAt()).isNull();
        assertThat(dto.getUserId()).isNull();
    }
}
