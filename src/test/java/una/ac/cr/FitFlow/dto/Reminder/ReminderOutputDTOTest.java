package una.ac.cr.FitFlow.dto.Reminder;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderOutputDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        OffsetDateTime now = OffsetDateTime.now();

        ReminderOutputDTO dto = ReminderOutputDTO.builder()
                .id(1L)
                .userId(10L)
                .habitId(20L)
                .message("Drink water")
                .time(now)
                .frequency("DAILY")
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUserId()).isEqualTo(10L);
        assertThat(dto.getHabitId()).isEqualTo(20L);
        assertThat(dto.getMessage()).isEqualTo("Drink water");
        assertThat(dto.getTime()).isEqualTo(now);
        assertThat(dto.getFrequency()).isEqualTo("DAILY");
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        OffsetDateTime time = OffsetDateTime.now();

        ReminderOutputDTO dto = new ReminderOutputDTO(
                2L,
                11L,
                21L,
                "Read a book",
                time,
                "WEEKLY"
        );

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getUserId()).isEqualTo(11L);
        assertThat(dto.getHabitId()).isEqualTo(21L);
        assertThat(dto.getMessage()).isEqualTo("Read a book");
        assertThat(dto.getTime()).isEqualTo(time);
        assertThat(dto.getFrequency()).isEqualTo("WEEKLY");
    }

    @Test
    void setters_updateValuesCorrectly() {
        OffsetDateTime time = OffsetDateTime.now();
        ReminderOutputDTO dto = new ReminderOutputDTO();

        dto.setId(3L);
        dto.setUserId(12L);
        dto.setHabitId(22L);
        dto.setMessage("Go jogging");
        dto.setTime(time);
        dto.setFrequency("MONTHLY");

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getUserId()).isEqualTo(12L);
        assertThat(dto.getHabitId()).isEqualTo(22L);
        assertThat(dto.getMessage()).isEqualTo("Go jogging");
        assertThat(dto.getTime()).isEqualTo(time);
        assertThat(dto.getFrequency()).isEqualTo("MONTHLY");
    }
}
