package una.ac.cr.FitFlow.dto.User;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserOutputDTOTest {

    @Test
    void builder_createsValidObject() {
        UserOutputDTO dto = UserOutputDTO.builder()
                .id(1L)
                .username("testuser")
                .email("user@example.com")
                .roleIds(Set.of(1L, 2L))
                .habitIds(Set.of(10L, 20L))
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getEmail()).isEqualTo("user@example.com");
        assertThat(dto.getRoleIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(dto.getHabitIds()).containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    void setters_updateFieldsCorrectly() {
        UserOutputDTO dto = new UserOutputDTO();
        dto.setId(2L);
        dto.setUsername("anotheruser");
        dto.setEmail("another@example.com");
        dto.setRoleIds(Set.of(3L));
        dto.setHabitIds(Set.of(30L, 40L));

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getUsername()).isEqualTo("anotheruser");
        assertThat(dto.getEmail()).isEqualTo("another@example.com");
        assertThat(dto.getRoleIds()).containsExactly(3L);
        assertThat(dto.getHabitIds()).containsExactlyInAnyOrder(30L, 40L);
    }

    @Test
    void noArgsConstructor_createsEmptyObject() {
        UserOutputDTO dto = new UserOutputDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getUsername()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getRoleIds()).isNull();
        assertThat(dto.getHabitIds()).isNull();
    }
}
