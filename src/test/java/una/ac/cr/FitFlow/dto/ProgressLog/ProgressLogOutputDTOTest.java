package una.ac.cr.FitFlow.dto.ProgressLog;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProgressLogOutputDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        OffsetDateTime now = OffsetDateTime.now();

        ProgressLogOutputDTO dto = ProgressLogOutputDTO.builder()
                .id(1L)
                .userId(10L)
                .routineId(20L)
                .date(now)
                .completedActivityIds(List.of(100L, 200L))
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUserId()).isEqualTo(10L);
        assertThat(dto.getRoutineId()).isEqualTo(20L);
        assertThat(dto.getDate()).isEqualTo(now);
        assertThat(dto.getCompletedActivityIds()).containsExactly(100L, 200L);
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        OffsetDateTime date = OffsetDateTime.now();
        List<Long> activities = List.of(5L, 6L);

        ProgressLogOutputDTO dto = new ProgressLogOutputDTO(2L, 11L, 21L, date, activities);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getUserId()).isEqualTo(11L);
        assertThat(dto.getRoutineId()).isEqualTo(21L);
        assertThat(dto.getDate()).isEqualTo(date);
        assertThat(dto.getCompletedActivityIds()).isEqualTo(activities);
    }

    @Test
    void setters_updateValuesCorrectly() {
        ProgressLogOutputDTO dto = new ProgressLogOutputDTO();
        OffsetDateTime date = OffsetDateTime.now();
        List<Long> activities = List.of(99L);

        dto.setId(3L);
        dto.setUserId(12L);
        dto.setRoutineId(22L);
        dto.setDate(date);
        dto.setCompletedActivityIds(activities);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getUserId()).isEqualTo(12L);
        assertThat(dto.getRoutineId()).isEqualTo(22L);
        assertThat(dto.getDate()).isEqualTo(date);
        assertThat(dto.getCompletedActivityIds()).isEqualTo(activities);
    }
}
