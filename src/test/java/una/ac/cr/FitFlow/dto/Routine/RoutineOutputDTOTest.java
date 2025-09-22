package una.ac.cr.FitFlow.dto.Routine;

import org.junit.jupiter.api.Test;
import una.ac.cr.FitFlow.model.Routine.DaysOfWeek;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoutineOutputDTOTest {

    @Test
    void builder_createsValidObject() {
        RoutineOutputDTO dto = RoutineOutputDTO.builder()
                .id(1L)
                .title("Morning Routine")
                .userId(10L)
                .daysOfWeek(List.of(DaysOfWeek.MON, DaysOfWeek.WED))
                .activityIds(List.of(100L, 200L))
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Morning Routine");
        assertThat(dto.getUserId()).isEqualTo(10L);
        assertThat(dto.getDaysOfWeek()).containsExactly(DaysOfWeek.MON, DaysOfWeek.WED);
        assertThat(dto.getActivityIds()).containsExactly(100L, 200L);
    }

    @Test
    void setters_updateFieldsCorrectly() {
        RoutineOutputDTO dto = new RoutineOutputDTO();
        dto.setId(2L);
        dto.setTitle("Evening Routine");
        dto.setUserId(20L);
        dto.setDaysOfWeek(List.of(DaysOfWeek.FRI, DaysOfWeek.SAT));
        dto.setActivityIds(List.of(300L));

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getTitle()).isEqualTo("Evening Routine");
        assertThat(dto.getUserId()).isEqualTo(20L);
        assertThat(dto.getDaysOfWeek()).containsExactly(DaysOfWeek.FRI, DaysOfWeek.SAT);
        assertThat(dto.getActivityIds()).containsExactly(300L);
    }

    @Test
    void noArgsConstructor_createsEmptyObject() {
        RoutineOutputDTO dto = new RoutineOutputDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getDaysOfWeek()).isNull();
        assertThat(dto.getActivityIds()).isNull();
    }
}
