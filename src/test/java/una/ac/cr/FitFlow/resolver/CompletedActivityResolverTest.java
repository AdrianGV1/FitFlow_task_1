package una.ac.cr.FitFlow.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityInputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityPageDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.CompletedActivity.CompletedActivityService;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogService;
import una.ac.cr.FitFlow.service.user.UserService;

@ExtendWith(MockitoExtension.class)
class CompletedActivityResolverTest {

    @Mock private CompletedActivityService completedActivityService;
    @Mock private HabitService habitService;
    @Mock private ProgressLogService progressLogService;
    @Mock private UserService userService;

    private CompletedActivityResolver newResolver() {
        return new CompletedActivityResolver(
                completedActivityService, habitService, progressLogService, userService);
    }

    // ----- Queries -----

    @Test
    @DisplayName("completedActivityById invoca requireRead y delega al service")
    void completedActivityById_ok() {
        CompletedActivityResolver resolver = newResolver();
        Long id = 10L;

        CompletedActivityOutputDTO dto = mock(CompletedActivityOutputDTO.class);
        when(completedActivityService.findCompletedActivityById(id)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            CompletedActivityOutputDTO out = resolver.completedActivityById(id);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.PROGRESO));
            verify(completedActivityService).findCompletedActivityById(id);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("completedActivities pagina y mapea a CompletedActivityPageDTO")
    void completedActivities_ok() {
        CompletedActivityResolver resolver = newResolver();
        int page = 1, size = 3;
        String keyword = "run";

        List<CompletedActivityOutputDTO> content = List.of(
                mock(CompletedActivityOutputDTO.class),
                mock(CompletedActivityOutputDTO.class)
        );
        Page<CompletedActivityOutputDTO> springPage =
                new PageImpl<>(content, PageRequest.of(page, size), 7);

        when(completedActivityService.listCompletedActivities(eq(keyword), any()))
                .thenReturn(springPage);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            CompletedActivityPageDTO out = resolver.completedActivities(page, size, keyword);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.PROGRESO));
            verify(completedActivityService).listCompletedActivities(eq(keyword), eq(PageRequest.of(page, size)));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(7);
            assertThat(out.getTotalPages()).isEqualTo( (int) Math.ceil(7.0 / size) );
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    @Test
    @DisplayName("completedActivitiesByUserId pagina y mapea correctamente")
    void completedActivitiesByUserId_ok() {
        CompletedActivityResolver resolver = newResolver();
        int page = 0, size = 2;
        Long userId = 55L;

        List<CompletedActivityOutputDTO> content = List.of(mock(CompletedActivityOutputDTO.class));
        Page<CompletedActivityOutputDTO> springPage =
                new PageImpl<>(content, PageRequest.of(page, size), 1);

        when(completedActivityService.findCompletedActivitiesByUserId(eq(userId), any()))
                .thenReturn(springPage);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            CompletedActivityPageDTO out = resolver.completedActivitiesByUserId(page, size, userId);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.PROGRESO));
            verify(completedActivityService).findCompletedActivitiesByUserId(eq(userId), eq(PageRequest.of(page, size)));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(1);
            assertThat(out.getTotalPages()).isEqualTo(1);
            assertThat(out.getPageNumber()).isEqualTo(0);
            assertThat(out.getPageSize()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("completedActivitiesByProgressLogId pagina y mapea correctamente")
    void completedActivitiesByProgressLogId_ok() {
        CompletedActivityResolver resolver = newResolver();
        int page = 2, size = 5;
        Long progressLogId = 9L;

        List<CompletedActivityOutputDTO> content = List.of(
                mock(CompletedActivityOutputDTO.class),
                mock(CompletedActivityOutputDTO.class)
        );
        Page<CompletedActivityOutputDTO> springPage =
                new PageImpl<>(content, PageRequest.of(page, size), 12);

        when(completedActivityService.findByProgressLogId(eq(progressLogId), any()))
                .thenReturn(springPage);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            CompletedActivityPageDTO out = resolver.completedActivitiesByProgressLogId(page, size, progressLogId);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.PROGRESO));
            verify(completedActivityService).findByProgressLogId(eq(progressLogId), eq(PageRequest.of(page, size)));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(12);
            assertThat(out.getTotalPages()).isEqualTo((int) Math.ceil(12.0 / size));
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    // ----- Mutations -----

    @Test
    @DisplayName("createCompletedActivity invoca requireWrite y delega al service")
    void createCompletedActivity_ok() {
        CompletedActivityResolver resolver = newResolver();
        CompletedActivityInputDTO input = mock(CompletedActivityInputDTO.class);
        CompletedActivityOutputDTO created = mock(CompletedActivityOutputDTO.class);

        when(completedActivityService.createCompletedActivity(input)).thenReturn(created);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            CompletedActivityOutputDTO out = resolver.createCompletedActivity(input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));
            verify(completedActivityService).createCompletedActivity(input);
            assertThat(out).isSameAs(created);
        }
    }

    @Test
    @DisplayName("updateCompletedActivity invoca requireWrite y delega al service")
    void updateCompletedActivity_ok() {
        CompletedActivityResolver resolver = newResolver();
        Long id = 7L;
        CompletedActivityInputDTO input = mock(CompletedActivityInputDTO.class);
        CompletedActivityOutputDTO updated = mock(CompletedActivityOutputDTO.class);

        when(completedActivityService.updateCompletedActivity(id, input)).thenReturn(updated);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            CompletedActivityOutputDTO out = resolver.updateCompletedActivity(id, input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));
            verify(completedActivityService).updateCompletedActivity(id, input);
            assertThat(out).isSameAs(updated);
        }
    }

    @Test
    @DisplayName("deleteCompletedActivity invoca requireWrite, llama al service y retorna true")
    void deleteCompletedActivity_ok() {
        CompletedActivityResolver resolver = newResolver();
        Long id = 99L;

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean out = resolver.deleteCompletedActivity(id);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));
            verify(completedActivityService).deleteCompletedActivity(id);
            assertThat(out).isTrue();
        }
    }

    // ----- SchemaMapping -----

    @Test
    @DisplayName("SchemaMapping 'habit' usa habitId del CA para resolver HabitOutputDTO")
    void schemaMapping_habit_ok() {
        CompletedActivityResolver resolver = newResolver();
        CompletedActivityOutputDTO ca = mock(CompletedActivityOutputDTO.class);
        when(ca.getHabitId()).thenReturn(123L);

        HabitOutputDTO habit = mock(HabitOutputDTO.class);
        when(habitService.findHabitById(123L)).thenReturn(habit);

        HabitOutputDTO out = resolver.habit(ca);

        verify(habitService).findHabitById(123L);
        assertThat(out).isSameAs(habit);
    }

    // Si luego reactivas el mapping de user, este test te sirve como base:
    @Test
    @DisplayName("monthlyCompletedActivitiesByCategoryAndDate invoca requireRead y delega al service")
    void monthlyCompletedActivitiesByCategoryAndDate_ok() {
        CompletedActivityResolver resolver = newResolver();
        String category = "PHYSICAL";
        OffsetDateTime date = OffsetDateTime.now();

        List<CompletedActivityOutputDTO> list = List.of(
                mock(CompletedActivityOutputDTO.class),
                mock(CompletedActivityOutputDTO.class)
        );
        when(completedActivityService.monthlyCompletedActivitiesByCategoryAndDate(category, date))
                .thenReturn(list);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            List<CompletedActivityOutputDTO> out =
                    resolver.monthlyCompletedActivitiesByCategoryAndDate(category, date);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.PROGRESO));
            verify(completedActivityService).monthlyCompletedActivitiesByCategoryAndDate(category, date);
            assertThat(out).isEqualTo(list);
        }
    }
}
