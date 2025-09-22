package una.ac.cr.FitFlow.dto.Role;

import org.junit.jupiter.api.Test;
import una.ac.cr.FitFlow.model.Role;

import static org.assertj.core.api.Assertions.assertThat;

class RoleOutputDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        RoleOutputDTO dto = RoleOutputDTO.builder()
                .id(1L)
                .name("ADMIN_ROLE")
                .permissions(Role.Permission.EDITOR)
                .module(Role.Module.RUTINAS)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("ADMIN_ROLE");
        assertThat(dto.getPermissions()).isEqualTo(Role.Permission.EDITOR);
        assertThat(dto.getModule()).isEqualTo(Role.Module.RUTINAS);
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        RoleOutputDTO dto = new RoleOutputDTO(
                2L,
                "USER_ROLE",
                Role.Permission.AUDITOR,
                Role.Module.GUIAS
        );

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("USER_ROLE");
        assertThat(dto.getPermissions()).isEqualTo(Role.Permission.AUDITOR);
        assertThat(dto.getModule()).isEqualTo(Role.Module.GUIAS);
    }

    @Test
    void setters_updateValuesCorrectly() {
        RoleOutputDTO dto = new RoleOutputDTO();

        dto.setId(3L);
        dto.setName("MANAGER_ROLE");
        dto.setPermissions(Role.Permission.EDITOR);
        dto.setModule(Role.Module.PROGRESO);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getName()).isEqualTo("MANAGER_ROLE");
        assertThat(dto.getPermissions()).isEqualTo(Role.Permission.EDITOR);
        assertThat(dto.getModule()).isEqualTo(Role.Module.PROGRESO);
    }
}
