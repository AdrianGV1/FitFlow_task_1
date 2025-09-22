package una.ac.cr.FitFlow.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.Habit.HabitInputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitPageDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderOutputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.user.UserService;
import una.ac.cr.FitFlow.service.RoutineActivity.RoutineActivityService;
import una.ac.cr.FitFlow.service.CompletedActivity.CompletedActivityService;
import una.ac.cr.FitFlow.service.Reminder.ReminderService;
import una.ac.cr.FitFlow.service.Guide.GuideService;

@ExtendWith(MockitoExtension.class)
class HabitResolverTest {

    @Mock private HabitService habitService;
    @Mock private UserService userService;
    @Mock private RoutineActivityService routineActivityService;
    @Mock private CompletedActivityService completedActivityService;
    @Mock private ReminderService reminderService;
    @Mock private GuideService guideService;

    private HabitResolver newResolver() {
        return new HabitResolver(
            habitService,
            userService,
            routineActivityService,
            completedActivityService,
            reminderService,
            guideService
        );
    }

    // ================== Queries ==================

    @Test
    @DisplayName("habitById: requiere lectura y delega a HabitService")
    void habitById_ok() {
        HabitResolver resolver = newResolver();
        Long id = 100L;
        HabitOutputDTO dto = mock(HabitOutputDTO.class);
        when(habitService.findHabitById(id)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            HabitOutputDTO out = resolver.habitById(id);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.ACTIVIDADES));
            verify(habitService).findHabitById(id);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("habits: pagina y mapea a HabitPageDTO")
    void habits_ok() {
        HabitResolver resolver = newResolver();
        int page = 1, size = 3;
        String keyword = "run";

        List<HabitOutputDTO> content = List.of(mock(HabitOutputDTO.class), mock(HabitOutputDTO.class));
        Page<HabitOutputDTO> p = new PageImpl<>(content, Pageable.ofSize(size).withPage(page), 9);

        when(habitService.listHabits(eq(keyword), any(Pageable.class))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            HabitPageDTO out = resolver.habits(page, size, keyword);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.ACTIVIDADES));
            verify(habitService).listHabits(eq(keyword), eq(Pageable.ofSize(size).withPage(page)));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(9);
            assertThat(out.getTotalPages()).isEqualTo((int)Math.ceil(9.0/size));
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    // ================== Mutations ==================

    @Test
    @DisplayName("createHabit: requiere escritura y delega a HabitService")
    void createHabit_ok() {
        HabitResolver resolver = newResolver();
        HabitInputDTO input = mock(HabitInputDTO.class);
        HabitOutputDTO created = mock(HabitOutputDTO.class);
        when(habitService.createHabit(input)).thenReturn(created);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            HabitOutputDTO out = resolver.createHabit(input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.ACTIVIDADES));
            verify(habitService).createHabit(input);
            assertThat(out).isSameAs(created);
        }
    }

    @Test
    @DisplayName("updateHabit: requiere escritura y delega a HabitService")
    void updateHabit_ok() {
        HabitResolver resolver = newResolver();
        Long id = 5L;
        HabitInputDTO input = mock(HabitInputDTO.class);
        HabitOutputDTO updated = mock(HabitOutputDTO.class);
        when(habitService.updateHabit(id, input)).thenReturn(updated);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            HabitOutputDTO out = resolver.updateHabit(id, input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.ACTIVIDADES));
            verify(habitService).updateHabit(id, input);
            assertThat(out).isSameAs(updated);
        }
    }

    @Test
    @DisplayName("deleteHabit: requiere escritura, llama al service y retorna true")
    void deleteHabit_ok() {
        HabitResolver resolver = newResolver();
        Long id = 9L;

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean out = resolver.deleteHabit(id);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.ACTIVIDADES));
            verify(habitService).deleteHabit(id);
            assertThat(out).isTrue();
        }
    }

    // ================== Relational Fields ==================

    @Test
    @DisplayName("@SchemaMapping users: delega a UserService.findUsersByHabitId")
    void users_ok() {
        HabitResolver resolver = newResolver();
        HabitOutputDTO habit = mock(HabitOutputDTO.class);
        when(habit.getId()).thenReturn(1L);

        List<UserOutputDTO> users = List.of(mock(UserOutputDTO.class), mock(UserOutputDTO.class));
        when(userService.findUsersByHabitId(1L)).thenReturn(users);

        List<UserOutputDTO> out = resolver.users(habit);

        verify(userService).findUsersByHabitId(1L);
        assertThat(out).isEqualTo(users);
    }

    @Test
    @DisplayName("@SchemaMapping routineActivities: usa RoutineActivityService.listByHabitId(...).getContent()")
    void routineActivities_ok() {
        HabitResolver resolver = newResolver();
        HabitOutputDTO habit = mock(HabitOutputDTO.class);
        when(habit.getId()).thenReturn(2L);

        List<RoutineActivityOutputDTO> content = List.of(
            mock(RoutineActivityOutputDTO.class),
            mock(RoutineActivityOutputDTO.class)
        );
        Page<RoutineActivityOutputDTO> page = new PageImpl<>(content, Pageable.unpaged(), content.size());
        when(routineActivityService.listByHabitId(2L, Pageable.unpaged())).thenReturn(page);

        List<RoutineActivityOutputDTO> out = resolver.routineActivities(habit);

        verify(routineActivityService).listByHabitId(2L, Pageable.unpaged());
        assertThat(out).isEqualTo(content);
    }

    @Test
    @DisplayName("@SchemaMapping completedActivities: delega a CompletedActivityService.findByHabitId")
    void completedActivities_ok() {
        HabitResolver resolver = newResolver();
        HabitOutputDTO habit = mock(HabitOutputDTO.class);
        when(habit.getId()).thenReturn(3L);

        List<CompletedActivityOutputDTO> list = List.of(
            mock(CompletedActivityOutputDTO.class),
            mock(CompletedActivityOutputDTO.class)
        );
        when(completedActivityService.findByHabitId(3L)).thenReturn(list);

        List<CompletedActivityOutputDTO> out = resolver.completedActivities(habit);

        verify(completedActivityService).findByHabitId(3L);
        assertThat(out).isEqualTo(list);
    }

    @Test
    @DisplayName("@SchemaMapping reminders: usa ReminderService.listByHabitId(...).getContent()")
    void reminders_ok() {
        HabitResolver resolver = newResolver();
        HabitOutputDTO habit = mock(HabitOutputDTO.class);
        when(habit.getId()).thenReturn(4L);

        List<ReminderOutputDTO> content = List.of(mock(ReminderOutputDTO.class));
        Page<ReminderOutputDTO> page = new PageImpl<>(content, Pageable.unpaged(), content.size());
        when(reminderService.listByHabitId(4L, Pageable.unpaged())).thenReturn(page);

        List<ReminderOutputDTO> out = resolver.reminders(habit);

        verify(reminderService).listByHabitId(4L, Pageable.unpaged());
        assertThat(out).isEqualTo(content);
    }

    @Test
    @DisplayName("@SchemaMapping guides: delega a GuideService.findByHabitId")
    void guides_ok() {
        HabitResolver resolver = newResolver();
        HabitOutputDTO habit = mock(HabitOutputDTO.class);
        when(habit.getId()).thenReturn(5L);

        List<GuideOutputDTO> list = List.of(mock(GuideOutputDTO.class));
        when(guideService.findByHabitId(5L)).thenReturn(list);

        List<GuideOutputDTO> out = resolver.guides(habit);

        verify(guideService).findByHabitId(5L);
        assertThat(out).isEqualTo(list);
    }
}
