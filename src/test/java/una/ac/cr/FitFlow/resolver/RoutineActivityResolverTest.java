package una.ac.cr.FitFlow.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityInputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityPageDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.Routine.RoutineService;
import una.ac.cr.FitFlow.service.RoutineActivity.RoutineActivityService;

@ExtendWith(MockitoExtension.class)
class RoutineActivityResolverTest {

    @Mock private RoutineActivityService routineActivityService;
    @Mock private RoutineService routineService;
    @Mock private HabitService habitService;

    private RoutineActivityResolver newResolver() {
        return new RoutineActivityResolver(routineActivityService, routineService, habitService);
    }

    // ================== Queries ==================

    @Test
    @DisplayName("routineActivityById: requiere lectura y delega al service")
    void routineActivityById_ok() {
        RoutineActivityResolver resolver = newResolver();
        Long id = 10L;
        RoutineActivityOutputDTO dto = mock(RoutineActivityOutputDTO.class);
        when(routineActivityService.findById(id)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutineActivityOutputDTO out = resolver.routineActivityById(id);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.ACTIVIDADES));
            verify(routineActivityService).findById(id);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("routineActivities: pagina y mapea a RoutineActivityPageDTO")
    void routineActivities_ok() {
        RoutineActivityResolver resolver = newResolver();
        int page = 1, size = 3;

        List<RoutineActivityOutputDTO> content = List.of(
                mock(RoutineActivityOutputDTO.class),
                mock(RoutineActivityOutputDTO.class)
        );
        Page<RoutineActivityOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 7);
        when(routineActivityService.list(PageRequest.of(page, size))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutineActivityPageDTO out = resolver.routineActivities(page, size);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.ACTIVIDADES));
            verify(routineActivityService).list(PageRequest.of(page, size));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(7L);
            assertThat(out.getTotalPages()).isEqualTo((int)Math.ceil(7.0/size));
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    @Test
    @DisplayName("routineActivitiesByRoutineId: pagina y mapea correctamente")
    void routineActivitiesByRoutineId_ok() {
        RoutineActivityResolver resolver = newResolver();
        Long routineId = 5L;
        int page = 0, size = 2;

        List<RoutineActivityOutputDTO> content = List.of(mock(RoutineActivityOutputDTO.class));
        Page<RoutineActivityOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 1);
        when(routineActivityService.listByRoutineId(routineId, PageRequest.of(page, size))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutineActivityPageDTO out = resolver.routineActivitiesByRoutineId(routineId, page, size);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.ACTIVIDADES));
            verify(routineActivityService).listByRoutineId(routineId, PageRequest.of(page, size));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(1L);
            assertThat(out.getTotalPages()).isEqualTo(1);
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    @Test
    @DisplayName("routineActivitiesByHabitId: pagina y mapea correctamente")
    void routineActivitiesByHabitId_ok() {
        RoutineActivityResolver resolver = newResolver();
        Long habitId = 9L;
        int page = 2, size = 4;

        List<RoutineActivityOutputDTO> content = List.of(
                mock(RoutineActivityOutputDTO.class),
                mock(RoutineActivityOutputDTO.class)
        );
        Page<RoutineActivityOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 10);
        when(routineActivityService.listByHabitId(habitId, PageRequest.of(page, size))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutineActivityPageDTO out = resolver.routineActivitiesByHabitId(habitId, page, size);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.ACTIVIDADES));
            verify(routineActivityService).listByHabitId(habitId, PageRequest.of(page, size));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(10L);
            assertThat(out.getTotalPages()).isEqualTo((int)Math.ceil(10.0/size));
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    // ================== Mutations ==================

    @Test
    @DisplayName("createRoutineActivity: requiere escritura y delega al service")
    void createRoutineActivity_ok() {
        RoutineActivityResolver resolver = newResolver();
        RoutineActivityInputDTO input = mock(RoutineActivityInputDTO.class);
        RoutineActivityOutputDTO created = mock(RoutineActivityOutputDTO.class);
        when(routineActivityService.create(input)).thenReturn(created);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutineActivityOutputDTO out = resolver.createRoutineActivity(input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.ACTIVIDADES));
            verify(routineActivityService).create(input);
            assertThat(out).isSameAs(created);
        }
    }

    @Test
    @DisplayName("updateRoutineActivity: requiere escritura y delega al service")
    void updateRoutineActivity_ok() {
        RoutineActivityResolver resolver = newResolver();
        Long id = 3L;
        RoutineActivityInputDTO input = mock(RoutineActivityInputDTO.class);
        RoutineActivityOutputDTO updated = mock(RoutineActivityOutputDTO.class);
        when(routineActivityService.update(id, input)).thenReturn(updated);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutineActivityOutputDTO out = resolver.updateRoutineActivity(id, input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.ACTIVIDADES));
            verify(routineActivityService).update(id, input);
            assertThat(out).isSameAs(updated);
        }
    }

    @Test
    @DisplayName("deleteRoutineActivity: requiere escritura, borra y retorna true")
    void deleteRoutineActivity_ok() {
        RoutineActivityResolver resolver = newResolver();
        Long id = 8L;

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean out = resolver.deleteRoutineActivity(id);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.ACTIVIDADES));
            verify(routineActivityService).delete(id);
            assertThat(out).isTrue();
        }
    }

    // ================== Schema mappings ==================

    @Test
    @DisplayName("@SchemaMapping routine: delega a RoutineService.findById con routineId del DTO")
    void schema_routine_ok() {
        RoutineActivityResolver resolver = newResolver();
        RoutineActivityOutputDTO ra = mock(RoutineActivityOutputDTO.class);
        when(ra.getRoutineId()).thenReturn(22L);

        RoutineOutputDTO routine = mock(RoutineOutputDTO.class);
        when(routineService.findById(22L)).thenReturn(routine);

        RoutineOutputDTO out = resolver.routine(ra);

        verify(routineService).findById(22L);
        assertThat(out).isSameAs(routine);
    }

    @Test
    @DisplayName("@SchemaMapping habit: delega a HabitService.findHabitById con habitId del DTO")
    void schema_habit_ok() {
        RoutineActivityResolver resolver = newResolver();
        RoutineActivityOutputDTO ra = mock(RoutineActivityOutputDTO.class);
        when(ra.getHabitId()).thenReturn(33L);

        HabitOutputDTO habit = mock(HabitOutputDTO.class);
        when(habitService.findHabitById(33L)).thenReturn(habit);

        HabitOutputDTO out = resolver.habit(ra);

        verify(habitService).findHabitById(33L);
        assertThat(out).isSameAs(habit);
    }
}
