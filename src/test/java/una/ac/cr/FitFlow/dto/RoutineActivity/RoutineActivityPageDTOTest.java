package una.ac.cr.FitFlow.dto.RoutineActivity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoutineActivityPageDTOTest {

    @Test
    void builder_createsValidObject() {
        RoutineActivityOutputDTO activity1 = RoutineActivityOutputDTO.builder()
                .id(1L)
                .routineId(10L)
                .habitId(20L)
                .duration(30)
                .notes("Morning yoga")
                .build();

        RoutineActivityOutputDTO activity2 = RoutineActivityOutputDTO.builder()
                .id(2L)
                .routineId(11L)
                .habitId(21L)
                .duration(45)
                .notes("Evening cardio")
                .build();

        RoutineActivityPageDTO page = RoutineActivityPageDTO.builder()
                .content(List.of(activity1, activity2))
                .totalElements(2L)
                .totalPages(1)
                .pageNumber(0)
                .pageSize(10)
                .build();

        assertThat(page.getContent()).containsExactly(activity1, activity2);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getPageNumber()).isEqualTo(0);
        assertThat(page.getPageSize()).isEqualTo(10);
    }

    @Test
    void setters_updateFieldsCorrectly() {
        RoutineActivityOutputDTO activity = RoutineActivityOutputDTO.builder()
                .id(3L)
                .routineId(12L)
                .habitId(22L)
                .duration(60)
                .notes("Stretching")
                .build();

        RoutineActivityPageDTO page = new RoutineActivityPageDTO();
        page.setContent(List.of(activity));
        page.setTotalElements(1L);
        page.setTotalPages(1);
        page.setPageNumber(1);
        page.setPageSize(5);

        assertThat(page.getContent()).containsExactly(activity);
        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getPageSize()).isEqualTo(5);
    }

    @Test
    void noArgsConstructor_createsEmptyObject() {
        RoutineActivityPageDTO page = new RoutineActivityPageDTO();

        assertThat(page.getContent()).isNull();
        assertThat(page.getTotalElements()).isEqualTo(0L);
        assertThat(page.getTotalPages()).isEqualTo(0);
        assertThat(page.getPageNumber()).isEqualTo(0);
        assertThat(page.getPageSize()).isEqualTo(0);
    }
}
