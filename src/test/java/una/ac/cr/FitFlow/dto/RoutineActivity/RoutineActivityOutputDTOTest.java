package una.ac.cr.FitFlow.dto.RoutineActivity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoutineActivityOutputDTOTest {

    @Test
    void builder_createsValidObject() {
        RoutineActivityOutputDTO dto = RoutineActivityOutputDTO.builder()
                .id(1L)
                .routineId(10L)
                .habitId(20L)
                .duration(30)
                .notes("Evening yoga session")
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRoutineId()).isEqualTo(10L);
        assertThat(dto.getHabitId()).isEqualTo(20L);
        assertThat(dto.getDuration()).isEqualTo(30);
        assertThat(dto.getNotes()).isEqualTo("Evening yoga session");
    }

    @Test
    void setters_updateFieldsCorrectly() {
        RoutineActivityOutputDTO dto = new RoutineActivityOutputDTO();
        dto.setId(2L);
        dto.setRoutineId(11L);
        dto.setHabitId(21L);
        dto.setDuration(40);
        dto.setNotes("Morning cardio");

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getRoutineId()).isEqualTo(11L);
        assertThat(dto.getHabitId()).isEqualTo(21L);
        assertThat(dto.getDuration()).isEqualTo(40);
        assertThat(dto.getNotes()).isEqualTo("Morning cardio");
    }

    @Test
    void noArgsConstructor_createsEmptyObject() {
        RoutineActivityOutputDTO dto = new RoutineActivityOutputDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getRoutineId()).isNull();
        assertThat(dto.getHabitId()).isNull();
        assertThat(dto.getDuration()).isNull();
        assertThat(dto.getNotes()).isNull();
    }
}
