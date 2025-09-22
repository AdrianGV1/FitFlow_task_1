package una.ac.cr.FitFlow.dto.Habit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HabitOutputDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        HabitOutputDTO dto = HabitOutputDTO.builder()
                .id(1L)
                .name("Read a book")
                .category("MENTAL")
                .description("Read 20 pages before bed")
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Read a book");
        assertThat(dto.getCategory()).isEqualTo("MENTAL");
        assertThat(dto.getDescription()).isEqualTo("Read 20 pages before bed");
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        HabitOutputDTO dto = new HabitOutputDTO(2L, "Run", "PHYSICAL", "Go for a 5km run");

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Run");
        assertThat(dto.getCategory()).isEqualTo("PHYSICAL");
        assertThat(dto.getDescription()).isEqualTo("Go for a 5km run");
    }

    @Test
    void setters_updateValuesCorrectly() {
        HabitOutputDTO dto = new HabitOutputDTO();

        dto.setId(3L);
        dto.setName("Meditation");
        dto.setCategory("MENTAL");
        dto.setDescription("10 minutes of meditation");

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getName()).isEqualTo("Meditation");
        assertThat(dto.getCategory()).isEqualTo("MENTAL");
        assertThat(dto.getDescription()).isEqualTo("10 minutes of meditation");
    }
}
