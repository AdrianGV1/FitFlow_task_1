package una.ac.cr.FitFlow.dto.Role;

import org.junit.jupiter.api.Test;
import una.ac.cr.FitFlow.model.Role;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RolePageDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        RoleOutputDTO role = RoleOutputDTO.builder()
                .id(1L)
                .name("ADMIN_ROLE")
                .permissions(Role.Permission.EDITOR)
                .module(Role.Module.RUTINAS)
                .build();

        RolePageDTO page = RolePageDTO.builder()
                .content(List.of(role))
                .totalElements(50L)
                .totalPages(5)
                .pageNumber(1)
                .pageSize(10)
                .build();

        assertThat(page.getContent()).containsExactly(role);
        assertThat(page.getTotalElements()).isEqualTo(50L);
        assertThat(page.getTotalPages()).isEqualTo(5);
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getPageSize()).isEqualTo(10);
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        RoleOutputDTO role = new RoleOutputDTO();
        List<RoleOutputDTO> list = List.of(role);

        RolePageDTO page = new RolePageDTO(list, 100L, 10, 2, 20);

        assertThat(page.getContent()).isEqualTo(list);
        assertThat(page.getTotalElements()).isEqualTo(100L);
        assertThat(page.getTotalPages()).isEqualTo(10);
        assertThat(page.getPageNumber()).isEqualTo(2);
        assertThat(page.getPageSize()).isEqualTo(20);
    }

    @Test
    void setters_updateValuesCorrectly() {
        RolePageDTO page = new RolePageDTO();
        RoleOutputDTO role = new RoleOutputDTO();
        List<RoleOutputDTO> list = List.of(role);

        page.setContent(list);
        page.setTotalElements(200L);
        page.setTotalPages(20);
        page.setPageNumber(3);
        page.setPageSize(15);

        assertThat(page.getContent()).isEqualTo(list);
        assertThat(page.getTotalElements()).isEqualTo(200L);
        assertThat(page.getTotalPages()).isEqualTo(20);
        assertThat(page.getPageNumber()).isEqualTo(3);
        assertThat(page.getPageSize()).isEqualTo(15);
    }
}
