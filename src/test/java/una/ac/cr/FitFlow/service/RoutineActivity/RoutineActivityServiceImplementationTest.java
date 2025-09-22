package una.ac.cr.FitFlow.service.RoutineActivity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityInputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForRoutineActivity;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.RoutineActivity;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.RoutineActivityRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;

@ExtendWith(MockitoExtension.class)
class RoutineActivityServiceImplementationTest {

    @Mock private RoutineActivityRepository routineActivityRepository;
    @Mock private RoutineRepository routineRepository;
    @Mock private HabitRepository habitRepository;
    @Mock private MapperForRoutineActivity mapper;

    @InjectMocks
    private RoutineActivityServiceImplementation service;

    private RoutineActivityInputDTO in(Long routineId, Long habitId, Integer duration, String notes) {
        RoutineActivityInputDTO dto = mock(RoutineActivityInputDTO.class);
        when(dto.getRoutineId()).thenReturn(routineId);
        when(dto.getHabitId()).thenReturn(habitId);
        when(dto.getDuration()).thenReturn(duration);
        when(dto.getNotes()).thenReturn(notes);
        return dto;
    }

    /* ============ CREATE ============ */

    @Test
    @DisplayName("create: feliz")
    void create_ok() {
        var dto = in(1L, 2L, 30, "nota");

        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(1L, 2L)).thenReturn(false);

        Routine routine = Routine.builder().id(1L).build();
        Habit habit = Habit.builder().id(2L).build();
        when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(habit));

        RoutineActivity entity = RoutineActivity.builder().id(10L).routine(routine).habit(habit).duration(30).notes("nota").build();
        when(mapper.toEntity(dto, routine, habit)).thenReturn(entity);
        when(routineActivityRepository.save(entity)).thenReturn(entity);

        RoutineActivityOutputDTO outDto = mock(RoutineActivityOutputDTO.class);
        when(mapper.toDto(entity)).thenReturn(outDto);

        RoutineActivityOutputDTO out = service.create(dto);

        assertThat(out).isSameAs(outDto);
        verify(routineActivityRepository).save(entity);
    }

    @Test
    @DisplayName("create: validaciones de requeridos")
    void create_required() {
        assertThatThrownBy(() -> service.create(in(null, 2L, 10, "n")))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("routineId");
        assertThatThrownBy(() -> service.create(in(1L, null, 10, "n")))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("habitId");
        assertThatThrownBy(() -> service.create(in(1L, 2L, null, "n")))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("duration");
        assertThatThrownBy(() -> service.create(in(1L, 2L, 0, "n")))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining(">= 1");
        assertThatThrownBy(() -> service.create(in(1L, 2L, -5, "n")))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining(">= 1");
    }

    @Test
    @DisplayName("create: ya existe (routineId, habitId)")
    void create_duplicate_pair() {
        var dto = in(1L, 2L, 10, "n");
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(1L, 2L)).thenReturn(true);
        assertThatThrownBy(() -> service.create(dto))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Ya existe esa actividad");
        verifyNoInteractions(routineRepository, habitRepository, mapper);
    }

    @Test
    @DisplayName("create: rutina/hábito no encontrados")
    void create_relations_not_found() {
        var dto = in(1L, 2L, 10, "n");
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(1L, 2L)).thenReturn(false);

        when(routineRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(dto))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Rutina no encontrada");

        when(routineRepository.findById(1L)).thenReturn(Optional.of(Routine.builder().id(1L).build()));
        when(habitRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(dto))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Hábito no encontrado");
    }

    /* ============ UPDATE ============ */

    @Test
    @DisplayName("update: feliz sin cambiar clave (routine/habit), solo duration/notes")
    void update_ok_no_key_change() {
        Long id = 9L;
        Routine r = Routine.builder().id(1L).build();
        Habit h = Habit.builder().id(2L).build();
        RoutineActivity current = RoutineActivity.builder().id(id).routine(r).habit(h).duration(10).notes("x").build();

        when(routineActivityRepository.findById(id)).thenReturn(Optional.of(current));

        var dto = in(null, null, 25, "y"); // no cambia routine/habit => key igual
        doNothing().when(mapper).copyToEntity(eq(dto), eq(current), isNull(), isNull());
        when(routineActivityRepository.save(current)).thenReturn(current);

        RoutineActivityOutputDTO outDto = mock(RoutineActivityOutputDTO.class);
        when(mapper.toDto(current)).thenReturn(outDto);

        RoutineActivityOutputDTO out = service.update(id, dto);

        assertThat(out).isSameAs(outDto);
        verify(routineRepository, never()).findById(any());
        verify(habitRepository, never()).findById(any());
        verify(routineActivityRepository).save(current);
    }

    @Test
    @DisplayName("update: cambia rutina y hábito → valida duplicado y resuelve relaciones")
    void update_ok_change_both() {
        Long id = 9L;
        RoutineActivity current = RoutineActivity.builder()
                .id(id)
                .routine(Routine.builder().id(1L).build())
                .habit(Habit.builder().id(2L).build())
                .duration(10)
                .build();

        when(routineActivityRepository.findById(id)).thenReturn(Optional.of(current));

        var dto = in(10L, 20L, 15, "nuevas notas");

        // keyChanged → verifica duplicado
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(10L, 20L)).thenReturn(false);

        Routine newRoutine = Routine.builder().id(10L).build();
        Habit newHabit = Habit.builder().id(20L).build();
        when(routineRepository.findById(10L)).thenReturn(Optional.of(newRoutine));
        when(habitRepository.findById(20L)).thenReturn(Optional.of(newHabit));

        doNothing().when(mapper).copyToEntity(eq(dto), eq(current), eq(newRoutine), eq(newHabit));
        when(routineActivityRepository.save(current)).thenReturn(current);

        when(mapper.toDto(current)).thenReturn(mock(RoutineActivityOutputDTO.class));

        service.update(id, dto);

        verify(routineRepository).findById(10L);
        verify(habitRepository).findById(20L);
        verify(routineActivityRepository).save(current);
    }

    @Test
    @DisplayName("update: duración inválida")
    void update_invalid_duration() {
        when(routineActivityRepository.findById(1L))
            .thenReturn(Optional.of(RoutineActivity.builder().id(1L).build()));

        assertThatThrownBy(() -> service.update(1L, in(null, null, 0, "n")))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining(">= 1");
        assertThatThrownBy(() -> service.update(1L, in(null, null, -3, "n")))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining(">= 1");
    }

    @Test
    @DisplayName("update: keyChanged y ya existe par (routineId, habitId) → lanza")
    void update_duplicate_key_after_change() {
        Long id = 1L;
        RoutineActivity current = RoutineActivity.builder()
                .id(id)
                .routine(Routine.builder().id(1L).build())
                .habit(Habit.builder().id(2L).build())
                .build();
        when(routineActivityRepository.findById(id)).thenReturn(Optional.of(current));

        var dto = in(10L, 20L, 5, "n");
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(10L, 20L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(id, dto))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Ya existe esa actividad");
        verify(routineRepository, never()).findById(any());
        verify(habitRepository, never()).findById(any());
    }

    @Test
    @DisplayName("update: nueva rutina/hábito no encontrados")
    void update_new_relations_not_found() {
        Long id = 1L;
        RoutineActivity current = RoutineActivity.builder().id(id).build();
        when(routineActivityRepository.findById(id)).thenReturn(Optional.of(current));

        // Cambia rutina
        var dtoR = in(77L, null, 5, "n");
        // keyChanged? Sí, porque current.getRoutine() == null y newRoutineId != null → true
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(77L, null)).thenReturn(false); // null no se usa realmente, pero mantenemos stub neutro
        when(routineRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, dtoR))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Rutina no encontrada");

        // Cambia hábito
        var dtoH = in(null, 88L, 5, "n");
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(null, 88L)).thenReturn(false);
        when(habitRepository.findById(88L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, dtoH))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Hábito no encontrado");
    }

    @Test
    @DisplayName("update: entidad no encontrada")
    void update_not_found() {
        when(routineActivityRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(99L, in(null, null, 5, null)))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no encontrada");
    }

    /* ============ DELETE ============ */

    @Test
    @DisplayName("delete: feliz")
    void delete_ok() {
        when(routineActivityRepository.existsById(7L)).thenReturn(true);
        service.delete(7L);
        verify(routineActivityRepository).deleteById(7L);
    }

    @Test
    @DisplayName("delete: no encontrada")
    void delete_not_found() {
        when(routineActivityRepository.existsById(7L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(7L))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no encontrada");
        verify(routineActivityRepository, never()).deleteById(anyLong());
    }

    /* ============ FIND & LIST ============ */

    @Test
    @DisplayName("findById: feliz y not found")
    void findById_ok_and_not_found() {
        RoutineActivity e = RoutineActivity.builder().id(3L).build();
        when(routineActivityRepository.findById(3L)).thenReturn(Optional.of(e));

        RoutineActivityOutputDTO dto = mock(RoutineActivityOutputDTO.class);
        when(mapper.toDto(e)).thenReturn(dto);

        assertThat(service.findById(3L)).isSameAs(dto);

        when(routineActivityRepository.findById(4L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(4L))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no encontrada");
    }

    @Test
    @DisplayName("list: delega en repo.findAll(pageable) y mapea")
    void list_ok() {
        var pageable = PageRequest.of(0, 2);
        RoutineActivity e1 = RoutineActivity.builder().id(1L).build();
        RoutineActivity e2 = RoutineActivity.builder().id(2L).build();

        when(routineActivityRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(List.of(e1, e2), pageable, 2));

        RoutineActivityOutputDTO d1 = mock(RoutineActivityOutputDTO.class);
        RoutineActivityOutputDTO d2 = mock(RoutineActivityOutputDTO.class);
        when(mapper.toDto(e1)).thenReturn(d1);
        when(mapper.toDto(e2)).thenReturn(d2);

        Page<RoutineActivityOutputDTO> out = service.list(pageable);
        assertThat(out.getContent()).containsExactly(d1, d2);
        assertThat(out.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("listByRoutineId: delega en repo.findByRoutine_Id y mapea")
    void listByRoutineId_ok() {
        var pageable = PageRequest.of(1, 3);
        RoutineActivity e = RoutineActivity.builder().id(1L).build();

        when(routineActivityRepository.findByRoutine_Id(99L, pageable))
            .thenReturn(new PageImpl<>(List.of(e), pageable, 1));

        RoutineActivityOutputDTO d = mock(RoutineActivityOutputDTO.class);
        when(mapper.toDto(e)).thenReturn(d);

        Page<RoutineActivityOutputDTO> out = service.listByRoutineId(99L, pageable);
        assertThat(out.getContent()).containsExactly(d);
    }

    @Test
    @DisplayName("listByHabitId: delega en repo.findByHabit_Id y mapea")
    void listByHabitId_ok() {
        var pageable = PageRequest.of(0, 5);
        RoutineActivity e = RoutineActivity.builder().id(1L).build();

        when(routineActivityRepository.findByHabit_Id(33L, pageable))
            .thenReturn(new PageImpl<>(List.of(e), pageable, 1));

        RoutineActivityOutputDTO d = mock(RoutineActivityOutputDTO.class);
        when(mapper.toDto(e)).thenReturn(d);

        Page<RoutineActivityOutputDTO> out = service.listByHabitId(33L, pageable);
        assertThat(out.getContent()).containsExactly(d);
    }
}
