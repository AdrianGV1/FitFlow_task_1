package una.ac.cr.FitFlow.dto.Guide;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GuideOutputDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        GuideOutputDTO dto = GuideOutputDTO.builder()
                .id(1L)
                .title("My Guide")
                .content("Guide content")
                .category("MENTAL")
                .recommendedHabitIds(Set.of(10L, 20L))
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("My Guide");
        assertThat(dto.getContent()).isEqualTo("Guide content");
        assertThat(dto.getCategory()).isEqualTo("MENTAL");
        assertThat(dto.getRecommendedHabitIds()).containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        Set<Long> habits = Set.of(5L, 6L);
        GuideOutputDTO dto = new GuideOutputDTO(2L, "Title", "Content", "DIET", habits);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getTitle()).isEqualTo("Title");
        assertThat(dto.getContent()).isEqualTo("Content");
        assertThat(dto.getCategory()).isEqualTo("DIET");
        assertThat(dto.getRecommendedHabitIds()).isEqualTo(habits);
    }

    @Test
    void setters_updateValuesCorrectly() {
        GuideOutputDTO dto = new GuideOutputDTO();
        Set<Long> habits = Set.of(99L);

        dto.setId(3L);
        dto.setTitle("Updated");
        dto.setContent("Updated content");
        dto.setCategory("SLEEP");
        dto.setRecommendedHabitIds(habits);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getTitle()).isEqualTo("Updated");
        assertThat(dto.getContent()).isEqualTo("Updated content");
        assertThat(dto.getCategory()).isEqualTo("SLEEP");
        assertThat(dto.getRecommendedHabitIds()).isEqualTo(habits);
    }
}
