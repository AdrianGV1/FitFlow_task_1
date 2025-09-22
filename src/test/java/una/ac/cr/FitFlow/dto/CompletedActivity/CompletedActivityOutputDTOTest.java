package una.ac.cr.FitFlow.dto.CompletedActivity;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompletedActivityOutputDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        OffsetDateTime now = OffsetDateTime.now();

        CompletedActivityOutputDTO dto = CompletedActivityOutputDTO.builder()
                .id(1L)
                .completedAt(now)
                .notes("Finished activity")
                .progressLogId(10L)
                .habitId(20L)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCompletedAt()).isEqualTo(now);
        assertThat(dto.getNotes()).isEqualTo("Finished activity");
        assertThat(dto.getProgressLogId()).isEqualTo(10L);
        assertThat(dto.getHabitId()).isEqualTo(20L);
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        OffsetDateTime date = OffsetDateTime.now();
        CompletedActivityOutputDTO dto = new CompletedActivityOutputDTO(2L, date, "Notes", 30L, 40L);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getCompletedAt()).isEqualTo(date);
        assertThat(dto.getNotes()).isEqualTo("Notes");
        assertThat(dto.getProgressLogId()).isEqualTo(30L);
        assertThat(dto.getHabitId()).isEqualTo(40L);
    }

    @Test
    void setters_updateValuesCorrectly() {
        CompletedActivityOutputDTO dto = new CompletedActivityOutputDTO();
        OffsetDateTime date = OffsetDateTime.now();

        dto.setId(3L);
        dto.setCompletedAt(date);
        dto.setNotes("Updated");
        dto.setProgressLogId(50L);
        dto.setHabitId(60L);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getCompletedAt()).isEqualTo(date);
        assertThat(dto.getNotes()).isEqualTo("Updated");
        assertThat(dto.getProgressLogId()).isEqualTo(50L);
        assertThat(dto.getHabitId()).isEqualTo(60L);
    }
}
