package una.ac.cr.FitFlow.service.Habit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import una.ac.cr.FitFlow.dto.Habit.HabitInputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForHabit;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.repository.HabitRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceImplementationTest {

    @Mock private HabitRepository habitRepository;
    @Mock private MapperForHabit mapper;

    @InjectMocks
    private HabitServiceImplementation service;

    private HabitInputDTO input(String name, String category) {
        HabitInputDTO in = new HabitInputDTO();
        in.setName(name);
        in.setCategory(category);
        return in;
    }

    private Habit aHabit(Long id, String name, Habit.Category cat) {
        return Habit.builder()
                .id(id)
                .name(name)
                .category(cat)
                .build();
    }

    // ---------- CREATE ----------
    @Test
    @DisplayName("createHabit: ok")
    void create_ok() {
        HabitInputDTO in = input("Caminar", Habit.Category.PHYSICAL.name());
        Habit entityToSave = aHabit(null, "Caminar", Habit.Category.PHYSICAL);
        Habit saved = aHabit(1L, "Caminar", Habit.Category.PHYSICAL);
        HabitOutputDTO out = new HabitOutputDTO();

        when(habitRepository.existsByName("Caminar")).thenReturn(false);
        when(mapper.toEntity(in)).thenReturn(entityToSave);
        when(habitRepository.save(entityToSave)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(out);

        HabitOutputDTO result = service.createHabit(in);

        assertSame(out, result);
        verify(habitRepository).existsByName("Caminar");
        verify(habitRepository).save(entityToSave);
    }

    @Test
    @DisplayName("createHabit: nombre nulo o en blanco → error")
    void create_nameBlank() {
        HabitInputDTO in1 = input(null, Habit.Category.MENTAL.name());
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> service.createHabit(in1));
        assertEquals("El nombre del hábito es obligatorio.", ex1.getMessage());

        HabitInputDTO in2 = input("   ", Habit.Category.MENTAL.name());
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> service.createHabit(in2));
        assertEquals("El nombre del hábito es obligatorio.", ex2.getMessage());

        verifyNoInteractions(habitRepository, mapper);
    }

    @Test
    @DisplayName("createHabit: categoría nula o en blanco → error")
    void create_categoryBlank() {
        HabitInputDTO in1 = input("Caminar", null);
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> service.createHabit(in1));
        assertEquals("La categoría es obligatoria.", ex1.getMessage());

        HabitInputDTO in2 = input("Caminar", "   ");
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> service.createHabit(in2));
        assertEquals("La categoría es obligatoria.", ex2.getMessage());

        verifyNoInteractions(habitRepository, mapper);
    }

    @Test
    @DisplayName("createHabit: nombre duplicado → error")
    void create_nameDuplicated() {
        HabitInputDTO in = input("Caminar", Habit.Category.DIET.name());
        when(habitRepository.existsByName("Caminar")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createHabit(in));
        assertEquals("El nombre del hábito ya está en uso.", ex.getMessage());
        verify(habitRepository).existsByName("Caminar");
        verify(habitRepository, never()).save(any());
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("updateHabit: ok (sin cambiar nombre)")
    void update_ok_noNameChange() {
        Long id = 5L;
        Habit existing = aHabit(id, "Caminar", Habit.Category.PHYSICAL);
        HabitInputDTO in = input("Caminar", Habit.Category.PHYSICAL.name());
        HabitOutputDTO out = new HabitOutputDTO();

        when(habitRepository.findById(id)).thenReturn(Optional.of(existing));
        when(habitRepository.save(existing)).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(out);

        HabitOutputDTO res = service.updateHabit(id, in);

        assertSame(out, res);
        verify(habitRepository, never()).existsByName(anyString()); // no verifica duplicado si no cambió
        verify(mapper).copyToEntity(in, existing);
        verify(habitRepository).save(existing);
    }

    @Test
    @DisplayName("updateHabit: id no existe → error")
    void update_notFound() {
        when(habitRepository.findById(99L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateHabit(99L, input("Caminar", Habit.Category.PHYSICAL.name())));
        assertEquals("Hábito no encontrado.", ex.getMessage());
    }

    @Test
    @DisplayName("updateHabit: nombre en blanco → error")
    void update_blankName() {
        Long id = 5L;
        Habit existing = aHabit(id, "Caminar", Habit.Category.MENTAL);
        HabitInputDTO in = input("   ", Habit.Category.MENTAL.name());

        when(habitRepository.findById(id)).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateHabit(id, in));
        assertEquals("El nombre del hábito no puede ser vacío.", ex.getMessage());
        verify(habitRepository, never()).existsByName(anyString());
        verify(habitRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateHabit: cambia nombre a uno duplicado → error")
    void update_changeNameToDuplicated() {
        Long id = 5L;
        Habit existing = aHabit(id, "Caminar", Habit.Category.DIET);
        HabitInputDTO in = input("Correr", Habit.Category.DIET.name());

        when(habitRepository.findById(id)).thenReturn(Optional.of(existing));
        when(habitRepository.existsByName("Correr")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateHabit(id, in));
        assertEquals("El nombre del hábito ya está en uso por otro hábito.", ex.getMessage());

        verify(habitRepository).existsByName("Correr");
        verify(habitRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateHabit: ok (cambia nombre a uno disponible)")
    void update_ok_changeName() {
        Long id = 5L;
        Habit existing = aHabit(id, "Caminar", Habit.Category.SLEEP);
        HabitInputDTO in = input("Correr", Habit.Category.SLEEP.name());
        HabitOutputDTO out = new HabitOutputDTO();

        when(habitRepository.findById(id)).thenReturn(Optional.of(existing));
        when(habitRepository.existsByName("Correr")).thenReturn(false);
        when(habitRepository.save(existing)).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(out);

        HabitOutputDTO res = service.updateHabit(id, in);

        assertSame(out, res);
        verify(habitRepository).existsByName("Correr");
        verify(mapper).copyToEntity(in, existing);
        verify(habitRepository).save(existing);
    }

    // ---------- DELETE ----------
    @Test
    @DisplayName("deleteHabit: ok")
    void delete_ok() {
        Long id = 7L;
        Habit existing = aHabit(id, "Caminar", Habit.Category.MENTAL);
        when(habitRepository.findById(id)).thenReturn(Optional.of(existing));

        service.deleteHabit(id);

        verify(habitRepository).delete(existing);
    }

    @Test
    @DisplayName("deleteHabit: no existe → error")
    void delete_notFound() {
        when(habitRepository.findById(7L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.deleteHabit(7L));
        assertEquals("Hábito no encontrado.", ex.getMessage());
        verify(habitRepository, never()).delete(any());
    }

    // ---------- FIND BY ID ----------
    @Test
    @DisplayName("findHabitById: ok")
    void findById_ok() {
        Long id = 3L;
        Habit h = aHabit(id, "Caminar", Habit.Category.PHYSICAL);
        HabitOutputDTO out = new HabitOutputDTO();

        when(habitRepository.findById(id)).thenReturn(Optional.of(h));
        when(mapper.toDto(h)).thenReturn(out);

        HabitOutputDTO res = service.findHabitById(id);

        assertSame(out, res);
        verify(mapper).toDto(h);
    }

    @Test
    @DisplayName("findHabitById: no existe → error")
    void findById_notFound() {
        when(habitRepository.findById(3L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.findHabitById(3L));
        assertEquals("Hábito no encontrado.", ex.getMessage());
    }

    // ---------- LIST ----------
    @Test
    @DisplayName("listHabits: q vacío → findAll")
    void list_blank() {
        Pageable pageable = PageRequest.of(0, 10);
        Habit h1 = aHabit(1L, "Caminar", Habit.Category.PHYSICAL);
        Habit h2 = aHabit(2L, "Dormir", Habit.Category.SLEEP);

        when(habitRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(h1, h2)));
        HabitOutputDTO o1 = new HabitOutputDTO();
        HabitOutputDTO o2 = new HabitOutputDTO();
        when(mapper.toDto(h1)).thenReturn(o1);
        when(mapper.toDto(h2)).thenReturn(o2);

        Page<HabitOutputDTO> page = service.listHabits("  ", pageable);

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().containsAll(List.of(o1, o2)));
        verify(habitRepository).findAll(pageable);
    }

    @Test
    @DisplayName("listHabits: q es Category → findByCategory")
    void list_byCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        String q = "sleep";
        Habit.Category cat = Habit.Category.SLEEP;

        Habit h1 = aHabit(1L, "Dormir bien", cat);
        when(habitRepository.findByCategory(cat, pageable))
                .thenReturn(new PageImpl<>(List.of(h1)));
        HabitOutputDTO o1 = new HabitOutputDTO();
        when(mapper.toDto(h1)).thenReturn(o1);

        Page<HabitOutputDTO> page = service.listHabits(q, pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(o1, page.getContent().get(0));
        verify(habitRepository).findByCategory(cat, pageable);
        verify(habitRepository, never()).findByNameContainingIgnoreCase(anyString(), any());
    }

    @Test
    @DisplayName("listHabits: q no es Category → findByNameContainingIgnoreCase")
    void list_byNameContains() {
        Pageable pageable = PageRequest.of(0, 10);
        String q = "cam";

        Habit h1 = aHabit(1L, "Caminar", Habit.Category.PHYSICAL);
        when(habitRepository.findByNameContainingIgnoreCase(q, pageable))
                .thenReturn(new PageImpl<>(List.of(h1)));
        HabitOutputDTO o1 = new HabitOutputDTO();
        when(mapper.toDto(h1)).thenReturn(o1);

        Page<HabitOutputDTO> page = service.listHabits(q, pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(o1, page.getContent().get(0));
        verify(habitRepository).findByNameContainingIgnoreCase(q, pageable);
    }
}