package una.ac.cr.FitFlow.dto.Reminder;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderPageDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        ReminderOutputDTO reminder = ReminderOutputDTO.builder()
                .id(1L)
                .userId(10L)
                .habitId(20L)
                .message("Drink water")
                .time(OffsetDateTime.now())
                .frequency("DAILY")
                .build();

        ReminderPageDTO page = ReminderPageDTO.builder()
                .content(List.of(reminder))
                .totalElements(50L)
                .totalPages(5)
                .pageNumber(1)
                .pageSize(10)
                .build();

        assertThat(page.getContent()).containsExactly(reminder);
        assertThat(page.getTotalElements()).isEqualTo(50L);
        assertThat(page.getTotalPages()).isEqualTo(5);
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getPageSize()).isEqualTo(10);
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        ReminderOutputDTO reminder = new ReminderOutputDTO();
        List<ReminderOutputDTO> list = List.of(reminder);

        ReminderPageDTO page = new ReminderPageDTO(list, 100L, 10, 2, 20);

        assertThat(page.getContent()).isEqualTo(list);
        assertThat(page.getTotalElements()).isEqualTo(100L);
        assertThat(page.getTotalPages()).isEqualTo(10);
        assertThat(page.getPageNumber()).isEqualTo(2);
        assertThat(page.getPageSize()).isEqualTo(20);
    }

    @Test
    void setters_updateValuesCorrectly() {
        ReminderPageDTO page = new ReminderPageDTO();
        ReminderOutputDTO reminder = new ReminderOutputDTO();
        List<ReminderOutputDTO> list = List.of(reminder);

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
