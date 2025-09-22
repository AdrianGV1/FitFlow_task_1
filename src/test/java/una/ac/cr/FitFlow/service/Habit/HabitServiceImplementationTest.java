package una.ac.cr.FitFlow.service.Habit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import una.ac.cr.FitFlow.dto.Habit.HabitInputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForHabit;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.repository.HabitRepository;

@ExtendWith(MockitoExtension.class)
class HabitServiceImplementationTest {

    @Mock private HabitRepository habitRepository;
    @Mock private MapperForHabit mapper;

    @InjectMocks
    private HabitServiceImplementation service;

    /* ================== CREATE ================== */

    @Test
    @DisplayName("createHabit: feliz")
    void create_ok() {
        HabitInputDTO in = mock(HabitInputDTO.class);
        when(in.getName()).thenReturn("Beber agua");
        when(in.getCategory()).thenReturn("DIET");

        when(habitRepository.existsByName("Beber agua")).thenReturn(false);

        Habit entity = Habit.builder().id(1L).name("Beber agua").category(Habit.Category.DIET).build();
        when(mapper.toEntity(in)).thenReturn(entity);

        Habit saved = Habit.builder().id(1L).name("Beber agua").category(Habit.Category.DIET).build();
        when(habitRepository.save(entity)).thenReturn(saved);

        HabitOutputDTO outDto = mock(HabitOutputDTO.class);
        when(mapper.toDto(saved)).thenReturn(outDto);

        HabitOutputDTO out = service.createHabit(in);

        assertThat(out).isSameAs(outDto);
        verify(habitRepository).save(entity);
    }

    @Test
    @DisplayName("createHabit: falla si name es null o en blanco")
    void create_fail_name_required() {
        HabitInputDTO in1 = mock(HabitInputDTO.class);
        when(in1.getName()).thenReturn(null);

        assertThatThrownBy(() -> service.createHabit(in1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El nombre del hábito es obligatorio");

        HabitInputDTO in2 = mock(HabitInputDTO.class);
        when(in2.getName()).thenReturn("   ");

        assertThatThrownBy(() -> service.createHabit(in2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El nombre del hábito es obligatorio");
    }

    @Test
    @DisplayName("createHabit: falla si category es null o en blanco")
    void create_fail_category_required() {
        HabitInputDTO in = mock(HabitInputDTO.class);
        when(in.getName()).thenReturn("Dormir 8h");
        when(in.getCategory()).thenReturn("  ");

        assertThatThrownBy(() -> service.createHabit(in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("La categoría es obligatoria");
    }

    @Test
    @DisplayName("createHabit: falla si name ya existe")
    void create_fail_duplicate_name() {
        HabitInputDTO in = mock(HabitInputDTO.class);
        when(in.getName()).thenReturn("Correr");
        when(in.getCategory()).thenReturn("PHYSICAL");

        when(habitRepository.existsByName("Correr")).thenReturn(true);

        assertThatThrownBy(() -> service.createHabit(in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ya está en uso");
        verify(mapper, never()).toEntity(any());
    }

    /* ================== UPDATE ================== */

    @Test
    @DisplayName("updateHabit: feliz (cambia nombre sin conflicto)")
    void update_ok() {
        Long id = 5L;

        Habit existing = Habit.builder().id(id).name("Viejo").category(Habit.Category.MENTAL).build();
        when(habitRepository.findById(id)).thenReturn(Optional.of(existing));

        HabitInputDTO in = mock(HabitInputDTO.class);
        when(in.getName()).thenReturn("Nuevo");           // cambia nombre
        when(in.getCategory()).thenReturn("MENTAL");      // opcional, igual
        when(habitRepository.existsByName("Nuevo")).thenReturn(false);

        // copyToEntity aplica cambios sobre existing
        doAnswer(inv -> {
            HabitInputDTO src = inv.getArgument(0);
            Habit target = inv.getArgument(1);
            if (src.getName() != null) target.setName(src.getName());
            // si tu mapper hace parsing de category, puedes simularlo también
            return null;
        }).when(mapper).copyToEntity(eq(in), eq(existing));

        when(habitRepository.save(existing)).thenReturn(existing);
        HabitOutputDTO outDto = mock(HabitOutputDTO.class);
        when(mapper.toDto(existing)).thenReturn(outDto);

        HabitOutputDTO out = service.updateHabit(id, in);

        assertThat(out).isSameAs(outDto);
        assertThat(existing.getName()).isEqualTo("Nuevo");
        verify(habitRepository).save(existing);
    }

    @Test
    @DisplayName("updateHabit: falla si no existe el hábito")
    void update_not_found() {
        when(habitRepository.findById(99L)).thenReturn(Optional.empty());

        HabitInputDTO in = mock(HabitInputDTO.class);
        assertThatThrownBy(() -> service.updateHabit(99L, in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Hábito no encontrado");
    }

    @Test
    @DisplayName("updateHabit: falla si name es vacío cuando viene informado")
    void update_fail_blank_name() {
        Long id = 1L;
        Habit existing = Habit.builder().id(id).name("Existente").build();
        when(habitRepository.findById(id)).thenReturn(Optional.of(existing));

        HabitInputDTO in = mock(HabitInputDTO.class);
        when(in.getName()).thenReturn("  ");  // informado pero en blanco

        assertThatThrownBy(() -> service.updateHabit(id, in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no puede ser vacío");
        verify(habitRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateHabit: falla si cambia nombre y ya existe en otro hábito")
    void update_fail_duplicate_when_changing_name() {
        Long id = 2L;
        Habit existing = Habit.builder().id(id).name("A").build();
        when(habitRepository.findById(id)).thenReturn(Optional.of(existing));

        HabitInputDTO in = mock(HabitInputDTO.class);
        when(in.getName()).thenReturn("B"); // distinto a "A" → cambiando

        when(habitRepository.existsByName("B")).thenReturn(true);

        assertThatThrownBy(() -> service.updateHabit(id, in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ya está en uso por otro hábito");
        verify(habitRepository, never()).save(any());
    }

    /* ================== DELETE ================== */

    @Test
    @DisplayName("deleteHabit: feliz")
    void delete_ok() {
        Long id = 7L;
        Habit h = Habit.builder().id(id).build();
        when(habitRepository.findById(id)).thenReturn(Optional.of(h));

        service.deleteHabit(id);

        verify(habitRepository).delete(h);
    }

    @Test
    @DisplayName("deleteHabit: not found")
    void delete_not_found() {
        when(habitRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteHabit(7L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Hábito no encontrado");
    }

    /* ================== FIND BY ID ================== */

    @Test
    @DisplayName("findHabitById: feliz")
    void findById_ok() {
        Long id = 3L;
        Habit h = Habit.builder().id(id).build();
        when(habitRepository.findById(id)).thenReturn(Optional.of(h));

        HabitOutputDTO dto = mock(HabitOutputDTO.class);
        when(mapper.toDto(h)).thenReturn(dto);

        HabitOutputDTO out = service.findHabitById(id);

        assertThat(out).isSameAs(dto);
    }

    @Test
    @DisplayName("findHabitById: not found")
    void findById_not_found() {
        when(habitRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findHabitById(3L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Hábito no encontrado");
    }

    /* ================== LIST ================== */

    @Test
    @DisplayName("listHabits: q == null o en blanco → findAll(pageable)")
    void list_no_q() {
        var pageable = PageRequest.of(0, 2);

        Habit h1 = Habit.builder().id(1L).build();
        Habit h2 = Habit.builder().id(2L).build();

        when(habitRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(List.of(h1, h2), pageable, 2));

        HabitOutputDTO d1 = mock(HabitOutputDTO.class);
        HabitOutputDTO d2 = mock(HabitOutputDTO.class);
        when(mapper.toDto(h1)).thenReturn(d1);
        when(mapper.toDto(h2)).thenReturn(d2);

        Page<HabitOutputDTO> out1 = service.listHabits(null, pageable);
        Page<HabitOutputDTO> out2 = service.listHabits("   ", pageable);

        assertThat(out1.getContent()).containsExactly(d1, d2);
        assertThat(out2.getContent()).containsExactly(d1, d2);
    }

    @Test
    @DisplayName("listHabits: q es categoría válida → findByCategory")
    void list_category_q() {
        var pageable = PageRequest.of(1, 3);

        Habit h = Habit.builder().id(10L).category(Habit.Category.SLEEP).build();
        when(habitRepository.findByCategory(Habit.Category.SLEEP, pageable))
            .thenReturn(new PageImpl<>(List.of(h), pageable, 1));

        HabitOutputDTO d = mock(HabitOutputDTO.class);
        when(mapper.toDto(h)).thenReturn(d);

        Page<HabitOutputDTO> out = service.listHabits("sleep", pageable);

        assertThat(out.getContent()).containsExactly(d);
        verify(habitRepository).findByCategory(Habit.Category.SLEEP, pageable);
        verify(habitRepository, never()).findByNameContainingIgnoreCase(anyString(), any());
    }

    @Test
    @DisplayName("listHabits: q no es categoría → findByNameContainingIgnoreCase")
    void list_text_q() {
        var pageable = PageRequest.of(2, 4);

        Habit h = Habit.builder().id(3L).name("Tomar agua").build();
        when(habitRepository.findByNameContainingIgnoreCase("agua", pageable))
            .thenReturn(new PageImpl<>(List.of(h), pageable, 1));

        HabitOutputDTO d = mock(HabitOutputDTO.class);
        when(mapper.toDto(h)).thenReturn(d);

        Page<HabitOutputDTO> out = service.listHabits("agua", pageable);

        assertThat(out.getContent()).containsExactly(d);
        verify(habitRepository).findByNameContainingIgnoreCase("agua", pageable);
    }
}
