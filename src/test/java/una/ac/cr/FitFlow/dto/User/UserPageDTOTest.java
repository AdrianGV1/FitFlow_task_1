package una.ac.cr.FitFlow.dto.User;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserPageDTOTest {

    @Test
    void builder_createsValidObject() {
        UserOutputDTO user1 = UserOutputDTO.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .roleIds(Set.of(1L))
                .habitIds(Set.of(10L))
                .build();

        UserOutputDTO user2 = UserOutputDTO.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .roleIds(Set.of(2L))
                .habitIds(Set.of(20L, 30L))
                .build();

        UserPageDTO page = UserPageDTO.builder()
                .content(List.of(user1, user2))
                .totalElements(2L)
                .totalPages(1)
                .pageNumber(0)
                .pageSize(10)
                .build();

        assertThat(page.getContent()).containsExactly(user1, user2);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getPageNumber()).isEqualTo(0);
        assertThat(page.getPageSize()).isEqualTo(10);
    }

    @Test
    void setters_updateFieldsCorrectly() {
        UserOutputDTO user = UserOutputDTO.builder()
                .id(3L)
                .username("user3")
                .email("user3@example.com")
                .roleIds(Set.of(3L))
                .habitIds(Set.of(40L))
                .build();

        UserPageDTO page = new UserPageDTO();
        page.setContent(List.of(user));
        page.setTotalElements(1L);
        page.setTotalPages(1);
        page.setPageNumber(1);
        page.setPageSize(5);

        assertThat(page.getContent()).containsExactly(user);
        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getPageSize()).isEqualTo(5);
    }

    @Test
    void noArgsConstructor_createsEmptyObject() {
        UserPageDTO page = new UserPageDTO();

        assertThat(page.getContent()).isNull();
        assertThat(page.getTotalElements()).isEqualTo(0L);
        assertThat(page.getTotalPages()).isEqualTo(0);
        assertThat(page.getPageNumber()).isEqualTo(0);
        assertThat(page.getPageSize()).isEqualTo(0);
    }
}
