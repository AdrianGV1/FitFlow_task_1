package una.ac.cr.FitFlow.dto.Guide;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GuidePageDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        GuideOutputDTO guide = GuideOutputDTO.builder()
                .id(1L)
                .title("My Guide")
                .content("Some useful content")
                .category("MENTAL")
                .recommendedHabitIds(Set.of(10L, 20L))
                .build();

        GuidePageDTO page = GuidePageDTO.builder()
                .content(List.of(guide))
                .totalElements(50L)
                .totalPages(5)
                .pageNumber(1)
                .pageSize(10)
                .build();

        assertThat(page.getContent()).containsExactly(guide);
        assertThat(page.getTotalElements()).isEqualTo(50L);
        assertThat(page.getTotalPages()).isEqualTo(5);
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getPageSize()).isEqualTo(10);
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        GuideOutputDTO guide = new GuideOutputDTO();
        List<GuideOutputDTO> list = List.of(guide);

        GuidePageDTO page = new GuidePageDTO(list, 100L, 10, 2, 20);

        assertThat(page.getContent()).isEqualTo(list);
        assertThat(page.getTotalElements()).isEqualTo(100L);
        assertThat(page.getTotalPages()).isEqualTo(10);
        assertThat(page.getPageNumber()).isEqualTo(2);
        assertThat(page.getPageSize()).isEqualTo(20);
    }

    @Test
    void setters_updateValuesCorrectly() {
        GuidePageDTO page = new GuidePageDTO();
        GuideOutputDTO guide = new GuideOutputDTO();
        List<GuideOutputDTO> list = List.of(guide);

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
