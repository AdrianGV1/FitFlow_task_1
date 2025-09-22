package una.ac.cr.FitFlow.dto.ProgressLog;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProgressLogPageDTOTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        ProgressLogOutputDTO log = ProgressLogOutputDTO.builder()
                .id(1L)
                .userId(10L)
                .routineId(20L)
                .date(OffsetDateTime.now())
                .completedActivityIds(List.of(100L, 200L))
                .build();

        ProgressLogPageDTO page = ProgressLogPageDTO.builder()
                .content(List.of(log))
                .totalElements(50L)
                .totalPages(5)
                .pageNumber(1)
                .pageSize(10)
                .build();

        assertThat(page.getContent()).containsExactly(log);
        assertThat(page.getTotalElements()).isEqualTo(50L);
        assertThat(page.getTotalPages()).isEqualTo(5);
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getPageSize()).isEqualTo(10);
    }

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        ProgressLogOutputDTO log = new ProgressLogOutputDTO();
        List<ProgressLogOutputDTO> list = List.of(log);

        ProgressLogPageDTO page = new ProgressLogPageDTO(list, 100L, 10, 2, 20);

        assertThat(page.getContent()).isEqualTo(list);
        assertThat(page.getTotalElements()).isEqualTo(100L);
        assertThat(page.getTotalPages()).isEqualTo(10);
        assertThat(page.getPageNumber()).isEqualTo(2);
        assertThat(page.getPageSize()).isEqualTo(20);
    }

    @Test
    void setters_updateValuesCorrectly() {
        ProgressLogPageDTO page = new ProgressLogPageDTO();
        ProgressLogOutputDTO log = new ProgressLogOutputDTO();
        List<ProgressLogOutputDTO> list = List.of(log);

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
