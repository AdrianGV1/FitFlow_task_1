package una.ac.cr.FitFlow.dto.CompletedActivity;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompletedActivityPageDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        CompletedActivityOutputDTO item = CompletedActivityOutputDTO.builder()
                .id(1L)
                .completedAt(OffsetDateTime.now())
                .notes("Note 1")
                .progressLogId(10L)
                .habitId(20L)
                .build();

        CompletedActivityPageDTO page = CompletedActivityPageDTO.builder()
                .content(List.of(item))
                .totalElements(100L)
                .totalPages(10)
                .pageNumber(1)
                .pageSize(10)
                .build();

        assertThat(page.getContent()).containsExactly(item);
        assertThat(page.getTotalElements()).isEqualTo(100L);
        assertThat(page.getTotalPages()).isEqualTo(10);
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getPageSize()).isEqualTo(10);
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        CompletedActivityOutputDTO item = new CompletedActivityOutputDTO();
        List<CompletedActivityOutputDTO> list = List.of(item);

        CompletedActivityPageDTO page = new CompletedActivityPageDTO(list, 50L, 5, 2, 20);

        assertThat(page.getContent()).isEqualTo(list);
        assertThat(page.getTotalElements()).isEqualTo(50L);
        assertThat(page.getTotalPages()).isEqualTo(5);
        assertThat(page.getPageNumber()).isEqualTo(2);
        assertThat(page.getPageSize()).isEqualTo(20);
    }

    @Test
    void setters_updateValuesCorrectly() {
        CompletedActivityPageDTO page = new CompletedActivityPageDTO();
        CompletedActivityOutputDTO item = new CompletedActivityOutputDTO();
        List<CompletedActivityOutputDTO> list = List.of(item);

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
