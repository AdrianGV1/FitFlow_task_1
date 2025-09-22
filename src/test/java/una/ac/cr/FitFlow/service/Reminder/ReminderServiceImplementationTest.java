package una.ac.cr.FitFlow.service.Reminder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import una.ac.cr.FitFlow.dto.Reminder.ReminderInputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForReminder;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.Reminder;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.ReminderRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReminderServiceImplementationTest {

    @Mock private ReminderRepository reminderRepository;
    @Mock private UserRepository userRepository;
    @Mock private HabitRepository habitRepository;
    @Mock private MapperForReminder mapper;

    @InjectMocks
    private ReminderServiceImplementation service;

    private ReminderInputDTO in(Long userId, Long habitId, String message, OffsetDateTime time, String freq) {
        ReminderInputDTO dto = mock(ReminderInputDTO.class);
        when(dto.getUserId()).thenReturn(userId);
        when(dto.getHabitId()).thenReturn(habitId);
        when(dto.getMessage()).thenReturn(message);
        when(dto.getTime()).thenReturn(time);
        when(dto.getFrequency()).thenReturn(freq);
        return dto;
    }

    /* ================== CREATE ================== */
    @Test
    @DisplayName("create: feliz")
    void create_ok() {
        var dto = in(1L, 2L, "tomar agua", OffsetDateTime.now(), "daily");

        User u = User.builder().id(1L).build();
        Habit h = Habit.builder().id(2L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(h));

        Reminder entity = Reminder.builder().id(10L).user(u).habit(h)
                .message("tomar agua").time(dto.getTime())
                .frequency(Reminder.Frequency.DAILY).build();

        when(mapper.toEntity(dto, u, h, Reminder.Frequency.DAILY)).thenReturn(entity);
        when(reminderRepository.save(entity)).thenReturn(entity);

        ReminderOutputDTO outDto = mock(ReminderOutputDTO.class);
        when(mapper.toDto(entity)).thenReturn(outDto);

        ReminderOutputDTO out = service.create(dto);
        assertThat(out).isSameAs(outDto);

        verify(reminderRepository).save(entity);
    }

    @Test
    @DisplayName("create: validaciones de requeridos")
    void create_required_validations() {
        var now = OffsetDateTime.now();
        assertThatThrownBy(() -> service.create(in(null, 1L, "m", now, "DAILY")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("userId");
        assertThatThrownBy(() -> service.create(in(1L, null, "m", now, "DAILY")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("habitId");
        assertThatThrownBy(() -> service.create(in(1L, 2L, "   ", now, "DAILY")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("message");
        assertThatThrownBy(() -> service.create(in(1L, 2L, "m", null, "DAILY")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("time");
        assertThatThrownBy(() -> service.create(in(1L, 2L, "m", now, "   ")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("frequency");
    }

    @Test
    @DisplayName("create: usuario/hábito no encontrados")
    void create_not_found_relations() {
        var dto = in(1L, 2L, "m", OffsetDateTime.now(), "WEEKLY");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Usuario no encontrado");

        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(habitRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Hábito no encontrado");
    }

    @Test
    @DisplayName("create: frecuencia inválida")
    void create_invalid_frequency() {
        var dto = in(1L, 2L, "m", OffsetDateTime.now(), "monthly");
        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(Habit.builder().id(2L).build()));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Frecuencia inválida");
    }

    /* ================== UPDATE ================== */
    @Test
    @DisplayName("update: feliz sin cambiar user/habit/frequency")
    void update_ok_no_changes() {
        Long id = 9L;
        Reminder current = Reminder.builder().id(id)
                .user(User.builder().id(1L).build())
                .habit(Habit.builder().id(2L).build())
                .message("m").time(OffsetDateTime.now())
                .frequency(Reminder.Frequency.WEEKLY).build();

        when(reminderRepository.findById(id)).thenReturn(Optional.of(current));

        var dto = in(1L, 2L, "nuevo", OffsetDateTime.now(), null); // frequency null -> no change
        doNothing().when(mapper).copyToEntity(eq(dto), eq(current), isNull(), isNull(), isNull());

        when(reminderRepository.save(current)).thenReturn(current);
        ReminderOutputDTO outDto = mock(ReminderOutputDTO.class);
        when(mapper.toDto(current)).thenReturn(outDto);

        ReminderOutputDTO out = service.update(id, dto);

        assertThat(out).isSameAs(outDto);
        verify(userRepository, never()).findById(any());
        verify(habitRepository, never()).findById(any());
        verify(reminderRepository).save(current);
    }

    @Test
    @DisplayName("update: cambia user/habit/frequency → busca, valida y copia")
    void update_ok_with_changes() {
        Long id = 9L;
        Reminder current = Reminder.builder().id(id)
                .user(User.builder().id(1L).build())
                .habit(Habit.builder().id(2L).build())
                .message("m").time(OffsetDateTime.now())
                .frequency(Reminder.Frequency.DAILY).build();

        when(reminderRepository.findById(id)).thenReturn(Optional.of(current));

        var dto = in(10L, 20L, "nuevo", OffsetDateTime.now(), "weekly"); // cambios

        User uNew = User.builder().id(10L).build();
        Habit hNew = Habit.builder().id(20L).build();
        when(userRepository.findById(10L)).thenReturn(Optional.of(uNew));
        when(habitRepository.findById(20L)).thenReturn(Optional.of(hNew));

        doNothing().when(mapper).copyToEntity(eq(dto), eq(current), eq(uNew), eq(hNew), eq(Reminder.Frequency.WEEKLY));

        when(reminderRepository.save(current)).thenReturn(current);
        ReminderOutputDTO outDto = mock(ReminderOutputDTO.class);
        when(mapper.toDto(current)).thenReturn(outDto);

        ReminderOutputDTO out = service.update(id, dto);

        assertThat(out).isSameAs(outDto);
        verify(userRepository).findById(10L);
        verify(habitRepository).findById(20L);
        verify(reminderRepository).save(current);
    }

    @Test
    @DisplayName("update: recordatorio no encontrado")
    void update_not_found() {
        when(reminderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(99L, in(1L, 2L, "m", OffsetDateTime.now(), "DAILY")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Recordatorio no encontrado");
    }

    @Test
    @DisplayName("update: nuevo userId/habitId no existen")
    void update_new_relations_not_found() {
        Long id = 1L;
        Reminder current = Reminder.builder().id(id).build();
        when(reminderRepository.findById(id)).thenReturn(Optional.of(current));

        var dtoU = in(10L, null, "m", OffsetDateTime.now(), null);
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(id, dtoU))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Usuario no encontrado");

        var dtoH = in(null, 20L, "m", OffsetDateTime.now(), null);
        when(habitRepository.findById(20L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(id, dtoH))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Hábito no encontrado");
    }

    @Test
    @DisplayName("update: frecuencia inválida al cambiar")
    void update_invalid_frequency() {
        Long id = 1L;
        Reminder current = Reminder.builder().id(id).build();
        when(reminderRepository.findById(id)).thenReturn(Optional.of(current));

        var dto = in(null, null, "m", OffsetDateTime.now(), "monthly"); // inválida
        assertThatThrownBy(() -> service.update(id, dto))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Frecuencia inválida");
    }

    /* ================== DELETE ================== */
    @Test
    @DisplayName("delete: feliz")
    void delete_ok() {
        when(reminderRepository.existsById(7L)).thenReturn(true);
        service.delete(7L);
        verify(reminderRepository).deleteById(7L);
    }

    @Test
    @DisplayName("delete: no encontrado")
    void delete_not_found() {
        when(reminderRepository.existsById(7L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(7L))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Recordatorio no encontrado");
        verify(reminderRepository, never()).deleteById(anyLong());
    }

    /* ================== FIND / LIST ================== */
    @Test
    @DisplayName("findById: feliz y not found")
    void findById_ok_and_notFound() {
        Reminder r = Reminder.builder().id(3L).build();
        when(reminderRepository.findById(3L)).thenReturn(Optional.of(r));
        ReminderOutputDTO dto = mock(ReminderOutputDTO.class);
        when(mapper.toDto(r)).thenReturn(dto);

        assertThat(service.findById(3L)).isSameAs(dto);

        when(reminderRepository.findById(4L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(4L))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Recordatorio no encontrado");
    }

    @Test
    @DisplayName("list: findAll(pageable) y mapea")
    void list_ok() {
        var pageable = PageRequest.of(0, 2);
        Reminder r1 = Reminder.builder().id(1L).build();
        Reminder r2 = Reminder.builder().id(2L).build();
        when(reminderRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(r1, r2), pageable, 2));

        ReminderOutputDTO d1 = mock(ReminderOutputDTO.class);
        ReminderOutputDTO d2 = mock(ReminderOutputDTO.class);
        when(mapper.toDto(r1)).thenReturn(d1);
        when(mapper.toDto(r2)).thenReturn(d2);

        Page<ReminderOutputDTO> out = service.list(pageable);

        assertThat(out.getContent()).containsExactly(d1, d2);
        assertThat(out.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("listByUserId: delega y mapea")
    void listByUserId_ok() {
        var pageable = PageRequest.of(1, 3);
        Reminder r = Reminder.builder().id(1L).build();
        when(reminderRepository.findByUser_Id(9L, pageable))
                .thenReturn(new PageImpl<>(List.of(r), pageable, 1));

        ReminderOutputDTO d = mock(ReminderOutputDTO.class);
        when(mapper.toDto(r)).thenReturn(d);

        Page<ReminderOutputDTO> out = service.listByUserId(9L, pageable);
        assertThat(out.getContent()).containsExactly(d);
    }

    @Test
    @DisplayName("listByHabitId: delega y mapea")
    void listByHabitId_ok() {
        var pageable = PageRequest.of(0, 5);
        Reminder r = Reminder.builder().id(1L).build();
        when(reminderRepository.findByHabit_Id(33L, pageable))
                .thenReturn(new PageImpl<>(List.of(r), pageable, 1));

        ReminderOutputDTO d = mock(ReminderOutputDTO.class);
        when(mapper.toDto(r)).thenReturn(d);

        Page<ReminderOutputDTO> out = service.listByHabitId(33L, pageable);
        assertThat(out.getContent()).containsExactly(d);
    }
}
