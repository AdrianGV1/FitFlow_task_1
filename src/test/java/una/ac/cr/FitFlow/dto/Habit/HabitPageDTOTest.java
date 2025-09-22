package una.ac.cr.FitFlow.dto.Habit;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HabitPageDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        HabitOutputDTO habit = HabitOutputDTO.builder()
                .id(1L)
                .name("Morning Run")
                .category("PHYSICAL")
                .description("Go for a run every morning")
                .build();

        HabitPageDTO page = HabitPageDTO.builder()
                .content(List.of(habit))
                .totalElements(100L)
                .totalPages(10)
                .pageNumber(1)
                .pageSize(10)
                .build();

        assertThat(page.getContent()).containsExactly(habit);
        assertThat(page.getTotalElements()).isEqualTo(100L);
        assertThat(page.getTotalPages()).isEqualTo(10);
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getPageSize()).isEqualTo(10);
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        HabitOutputDTO habit = new HabitOutputDTO();
        List<HabitOutputDTO> list = List.of(habit);

        HabitPageDTO page = new HabitPageDTO(list, 50L, 5, 2, 20);

        assertThat(page.getContent()).isEqualTo(list);
        assertThat(page.getTotalElements()).isEqualTo(50L);
        assertThat(page.getTotalPages()).isEqualTo(5);
        assertThat(page.getPageNumber()).isEqualTo(2);
        assertThat(page.getPageSize()).isEqualTo(20);
    }

    @Test
    void setters_updateValuesCorrectly() {
        HabitPageDTO page = new HabitPageDTO();
        HabitOutputDTO habit = new HabitOutputDTO();
        List<HabitOutputDTO> list = List.of(habit);

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
