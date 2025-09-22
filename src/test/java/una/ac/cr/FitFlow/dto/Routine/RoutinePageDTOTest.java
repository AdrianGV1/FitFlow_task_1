package una.ac.cr.FitFlow.dto.Routine;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoutinePageDTOTest {

    @Test
    void builder_createsValidObject() {
        RoutineOutputDTO routine1 = RoutineOutputDTO.builder()
                .id(1L)
                .title("Morning Routine")
                .userId(10L)
                .build();

        RoutineOutputDTO routine2 = RoutineOutputDTO.builder()
                .id(2L)
                .title("Evening Routine")
                .userId(20L)
                .build();

        RoutinePageDTO page = RoutinePageDTO.builder()
                .content(List.of(routine1, routine2))
                .totalElements(2L)
                .totalPages(1)
                .pageNumber(0)
                .pageSize(10)
                .build();

        assertThat(page.getContent()).containsExactly(routine1, routine2);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getPageNumber()).isEqualTo(0);
        assertThat(page.getPageSize()).isEqualTo(10);
    }

    @Test
    void setters_updateFieldsCorrectly() {
        RoutineOutputDTO routine = RoutineOutputDTO.builder()
                .id(3L)
                .title("Yoga Routine")
                .userId(30L)
                .build();

        RoutinePageDTO page = new RoutinePageDTO();
        page.setContent(List.of(routine));
        page.setTotalElements(1L);
        page.setTotalPages(1);
        page.setPageNumber(1);
        page.setPageSize(5);

        assertThat(page.getContent()).containsExactly(routine);
        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getPageSize()).isEqualTo(5);
    }

    @Test
    void noArgsConstructor_createsEmptyObject() {
        RoutinePageDTO page = new RoutinePageDTO();

        assertThat(page.getContent()).isNull();
        assertThat(page.getTotalElements()).isEqualTo(0L);
        assertThat(page.getTotalPages()).isEqualTo(0);
        assertThat(page.getPageNumber()).isEqualTo(0);
        assertThat(page.getPageSize()).isEqualTo(0);
    }
}
