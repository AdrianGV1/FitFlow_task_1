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
import una.ac.cr.FitFlow.dto.Reminder.ReminderInputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderOutputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderPageDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.Reminder.ReminderService;
import una.ac.cr.FitFlow.service.user.UserService;

@ExtendWith(MockitoExtension.class)
class ReminderResolverTest {

    @Mock private ReminderService reminderService;
    @Mock private UserService userService;
    @Mock private HabitService habitService;

    private ReminderResolver newResolver() {
        return new ReminderResolver(reminderService, userService, habitService);
    }

    // =============== Queries ===============

    @Test
    @DisplayName("reminderById: requiere lectura y delega a service.findById")
    void reminderById_ok() {
        ReminderResolver resolver = newResolver();
        Long id = 10L;
        ReminderOutputDTO dto = mock(ReminderOutputDTO.class);
        when(reminderService.findById(id)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            ReminderOutputDTO out = resolver.reminderById(id);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RECORDATORIOS));
            verify(reminderService).findById(id);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("reminders: pagina y mapea a ReminderPageDTO")
    void reminders_ok() {
        ReminderResolver resolver = newResolver();
        int page = 1, size = 3;

        List<ReminderOutputDTO> content = List.of(
            mock(ReminderOutputDTO.class),
            mock(ReminderOutputDTO.class)
        );
        Page<ReminderOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 7);
        when(reminderService.list(PageRequest.of(page, size))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            ReminderPageDTO out = resolver.reminders(page, size);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RECORDATORIOS));
            verify(reminderService).list(PageRequest.of(page, size));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(7L);
            assertThat(out.getTotalPages()).isEqualTo((int)Math.ceil(7.0/size));
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    @Test
    @DisplayName("remindersByUserId: pagina y mapea a ReminderPageDTO")
    void remindersByUserId_ok() {
        ReminderResolver resolver = newResolver();
        Long userId = 55L;
        int page = 0, size = 2;

        List<ReminderOutputDTO> content = List.of(mock(ReminderOutputDTO.class));
        Page<ReminderOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 1);
        when(reminderService.listByUserId(userId, PageRequest.of(page, size))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            ReminderPageDTO out = resolver.remindersByUserId(userId, page, size);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RECORDATORIOS));
            verify(reminderService).listByUserId(userId, PageRequest.of(page, size));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(1L);
            assertThat(out.getTotalPages()).isEqualTo(1);
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    // =============== Mutations ===============

    @Test
    @DisplayName("createReminder: requiere escritura y delega a service.create")
    void createReminder_ok() {
        ReminderResolver resolver = newResolver();
        ReminderInputDTO input = mock(ReminderInputDTO.class);
        ReminderOutputDTO created = mock(ReminderOutputDTO.class);
        when(reminderService.create(input)).thenReturn(created);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            ReminderOutputDTO out = resolver.createReminder(input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RECORDATORIOS));
            verify(reminderService).create(input);
            assertThat(out).isSameAs(created);
        }
    }

    @Test
    @DisplayName("updateReminder: requiere escritura y delega a service.update")
    void updateReminder_ok() {
        ReminderResolver resolver = newResolver();
        Long id = 7L;
        ReminderInputDTO input = mock(ReminderInputDTO.class);
        ReminderOutputDTO updated = mock(ReminderOutputDTO.class);
        when(reminderService.update(id, input)).thenReturn(updated);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            ReminderOutputDTO out = resolver.updateReminder(id, input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RECORDATORIOS));
            verify(reminderService).update(id, input);
            assertThat(out).isSameAs(updated);
        }
    }

    @Test
    @DisplayName("deleteReminder: requiere escritura, borra y retorna true")
    void deleteReminder_ok() {
        ReminderResolver resolver = newResolver();
        Long id = 9L;

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean out = resolver.deleteReminder(id);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RECORDATORIOS));
            verify(reminderService).delete(id);
            assertThat(out).isTrue();
        }
    }

    // =============== Schema mappings ===============

    @Test
    @DisplayName("@SchemaMapping user: delega a UserService.findUserById con userId del DTO")
    void schema_user_ok() {
        ReminderResolver resolver = newResolver();
        ReminderOutputDTO r = mock(ReminderOutputDTO.class);
        when(r.getUserId()).thenReturn(42L);

        UserOutputDTO user = mock(UserOutputDTO.class);
        when(userService.findUserById(42L)).thenReturn(user);

        UserOutputDTO out = resolver.user(r);

        verify(userService).findUserById(42L);
        assertThat(out).isSameAs(user);
    }

    @Test
    @DisplayName("@SchemaMapping habit: devuelve null si habitId es null")
    void schema_habit_null_ok() {
        ReminderResolver resolver = newResolver();
        ReminderOutputDTO r = mock(ReminderOutputDTO.class);
        when(r.getHabitId()).thenReturn(null);

        HabitOutputDTO out = resolver.habit(r);

        verifyNoInteractions(habitService);
        assertThat(out).isNull();
    }

    @Test
    @DisplayName("@SchemaMapping habit: delega a HabitService.findHabitById cuando habitId no es null")
    void schema_habit_ok() {
        ReminderResolver resolver = newResolver();
        ReminderOutputDTO r = mock(ReminderOutputDTO.class);
        when(r.getHabitId()).thenReturn(5L);

        HabitOutputDTO habit = mock(HabitOutputDTO.class);
        when(habitService.findHabitById(5L)).thenReturn(habit);

        HabitOutputDTO out = resolver.habit(r);

        verify(habitService).findHabitById(5L);
        assertThat(out).isSameAs(habit);
    }
}
