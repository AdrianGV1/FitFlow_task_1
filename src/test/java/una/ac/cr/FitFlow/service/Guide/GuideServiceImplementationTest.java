package una.ac.cr.FitFlow.service.Guide;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import una.ac.cr.FitFlow.dto.Guide.GuideInputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForGuide;
import una.ac.cr.FitFlow.model.Guide;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.repository.GuideRepository;
import una.ac.cr.FitFlow.repository.HabitRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuideServiceImplementationTest {

    @Mock private GuideRepository guideRepository;
    @Mock private HabitRepository habitRepository;
    @Mock private MapperForGuide mapper;

    @InjectMocks
    private GuideServiceImplementation service;

    // ---------- Helpers ----------
    private GuideInputDTO in(String title, String content, String category, Set<Long> rec) {
        GuideInputDTO dto = new GuideInputDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setCategory(category);
        dto.setRecommendedHabitIds(rec);
        return dto;
    }

    private Guide aGuide(Long id) {
        return Guide.builder().id(id).build();
    }

    private Habit aHabit(Long id) {
        return Habit.builder().id(id).name("H"+id).category(Habit.Category.PHYSICAL).build();
    }

    // ---------- CREATE ----------
    @Test
    @DisplayName("createGuide: ok con recommendedHabitIds válidos")
    void create_ok_withHabits() {
        Set<Long> ids = Set.of(1L, 2L);
        GuideInputDTO in = in("Título", "Contenido", "PHYSICAL", ids);

        Guide entity = Guide.builder().build();
        Guide saved  = aGuide(10L);
        GuideOutputDTO out = new GuideOutputDTO();

        when(mapper.toEntity(in)).thenReturn(entity);
        when(habitRepository.findAllById(ids)).thenReturn(List.of(aHabit(1L), aHabit(2L)));
        when(guideRepository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(out);

        GuideOutputDTO res = service.createGuide(in);

        assertSame(out, res);
        verify(habitRepository).findAllById(ids);
        verify(guideRepository).save(entity);
    }

    @Test
    @DisplayName("createGuide: recommendedHabitIds = null → no toca habitRepository")
    void create_ok_nullRecommended() {
        GuideInputDTO in = in("Título", "Contenido", "MENTAL", null);

        Guide entity = Guide.builder().build();
        Guide saved  = aGuide(1L);
        GuideOutputDTO out = new GuideOutputDTO();

        when(mapper.toEntity(in)).thenReturn(entity);
        when(guideRepository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(out);

        GuideOutputDTO res = service.createGuide(in);

        assertSame(out, res);
        verify(habitRepository, never()).findAllById(any());
        verify(guideRepository).save(entity);
    }

    @Test
    @DisplayName("createGuide: recommendedHabitIds vacío → set vacío en la entidad")
    void create_ok_emptyRecommended_setsEmpty() {
        GuideInputDTO in = in("Título", "Contenido", "SLEEP", Collections.emptySet());

        Guide entity = Guide.builder().build();
        Guide saved  = aGuide(2L);
        GuideOutputDTO out = new GuideOutputDTO();

        when(mapper.toEntity(in)).thenReturn(entity);
        when(guideRepository.save(any(Guide.class))).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(out);

        ArgumentCaptor<Guide> cap = ArgumentCaptor.forClass(Guide.class);

        GuideOutputDTO res = service.createGuide(in);

        assertSame(out, res);
        verify(habitRepository, never()).findAllById(any());
        verify(guideRepository).save(cap.capture());
        Guide toSave = cap.getValue();
        assertNotNull(toSave.getRecommendedHabits());
        assertTrue(toSave.getRecommendedHabits().isEmpty(), "Debe setearse un Set vacío cuando ids está vacío");
    }

    @Test
    @DisplayName("createGuide: hábitos inexistentes → IllegalArgumentException")
    void create_fail_missingHabits() {
        Set<Long> ids = Set.of(1L, 2L, 3L);
        GuideInputDTO in = in("Título", "Contenido", "DIET", ids);

        when(mapper.toEntity(in)).thenReturn(Guide.builder().build());
        // devuelve menos hábitos que ids → dispara validación
        when(habitRepository.findAllById(ids)).thenReturn(List.of(aHabit(1L), aHabit(3L)));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createGuide(in));
        assertTrue(ex.getMessage().toLowerCase().contains("no existen"));
        verify(guideRepository, never()).save(any());
    }

    @Test
    @DisplayName("createGuide: valida campos obligatorios (title/content/category)")
    void create_fail_requiredFields() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createGuide(in(" ", "c", "PHYSICAL", null)));
        assertThrows(IllegalArgumentException.class,
                () -> service.createGuide(in("t", " ", "PHYSICAL", null)));
        assertThrows(IllegalArgumentException.class,
                () -> service.createGuide(in("t", "c", " ", null)));
        verifyNoInteractions(guideRepository, habitRepository, mapper);
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("updateGuide: ok con ids de hábitos (reemplaza set)")
    void update_ok_withHabitsProvided() {
        Long id = 5L;
        Set<Long> ids = Set.of(7L, 8L);
        GuideInputDTO in = in("Nuevo", "Contenido", "MENTAL", ids);

        Guide current = aGuide(id);
        GuideOutputDTO out = new GuideOutputDTO();

        when(guideRepository.findById(id)).thenReturn(Optional.of(current));
        doNothing().when(mapper).copyToEntity(in, current);
        when(habitRepository.findAllById(ids)).thenReturn(List.of(aHabit(7L), aHabit(8L)));
        when(guideRepository.save(current)).thenReturn(current);
        when(mapper.toDto(current)).thenReturn(out);

        GuideOutputDTO res = service.updateGuide(id, in);

        assertSame(out, res);
        verify(habitRepository).findAllById(ids);
        verify(guideRepository).save(current);
    }

    @Test
    @DisplayName("updateGuide: recommendedHabitIds = null → no modifica hábitos")
    void update_ok_nullRecommended_keepsCurrent() {
        Long id = 6L;
        GuideInputDTO in = in("Nuevo", "Contenido", "PHYSICAL", null);

        Guide current = aGuide(id);
        GuideOutputDTO out = new GuideOutputDTO();

        when(guideRepository.findById(id)).thenReturn(Optional.of(current));
        doNothing().when(mapper).copyToEntity(in, current);
        when(guideRepository.save(current)).thenReturn(current);
        when(mapper.toDto(current)).thenReturn(out);

        GuideOutputDTO res = service.updateGuide(id, in);

        assertSame(out, res);
        verify(habitRepository, never()).findAllById(any());
        verify(guideRepository).save(current);
    }

    @Test
    @DisplayName("updateGuide: id no existe → IllegalArgumentException")
    void update_fail_notFound() {
        when(guideRepository.findById(999L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateGuide(999L, in("t", "c", "PHYSICAL", null)));
        assertTrue(ex.getMessage().contains("Guía no encontrada"));
        verify(guideRepository, never()).save(any());
    }

    // ---------- DELETE ----------
    @Test
    @DisplayName("deleteGuide: ok")
    void delete_ok() {
        Long id = 11L;
        Guide g = aGuide(id);
        when(guideRepository.findById(id)).thenReturn(Optional.of(g));

        service.deleteGuide(id);

        verify(guideRepository).delete(g);
    }

    @Test
    @DisplayName("deleteGuide: id no existe → IllegalArgumentException")
    void delete_fail_notFound() {
        when(guideRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.deleteGuide(404L));
        verify(guideRepository, never()).delete(any());
    }

    // ---------- FIND BY ID ----------
    @Test
    @DisplayName("findGuideById: ok")
    void findById_ok() {
        Long id = 33L;
        Guide g = aGuide(id);
        GuideOutputDTO out = new GuideOutputDTO();

        when(guideRepository.findById(id)).thenReturn(Optional.of(g));
        when(mapper.toDto(g)).thenReturn(out);

        GuideOutputDTO res = service.findGuideById(id);

        assertSame(out, res);
        verify(mapper).toDto(g);
    }

    @Test
    @DisplayName("findGuideById: no existe → IllegalArgumentException")
    void findById_fail_notFound() {
        when(guideRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.findGuideById(1L));
    }

    // ---------- LIST ----------
    @Test
    @DisplayName("listGuides: q vacío → findAll")
    void list_blank() {
        Pageable pageable = PageRequest.of(0, 10);
        Guide g1 = aGuide(1L);
        Guide g2 = aGuide(2L);

        when(guideRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(g1, g2)));
        GuideOutputDTO o1 = new GuideOutputDTO();
        GuideOutputDTO o2 = new GuideOutputDTO();
        when(mapper.toDto(g1)).thenReturn(o1);
        when(mapper.toDto(g2)).thenReturn(o2);

        Page<GuideOutputDTO> page = service.listGuides("  ", pageable);

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().containsAll(List.of(o1, o2)));
        verify(guideRepository).findAll(pageable);
    }

    @Test
    @DisplayName("listGuides: q es Category válida → findByCategory")
    void list_byCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        String q = "sleep";
        Guide.Category cat = Guide.Category.SLEEP;

        Guide g1 = aGuide(1L);
        when(guideRepository.findByCategory(cat, pageable)).thenReturn(new PageImpl<>(List.of(g1)));
        GuideOutputDTO o1 = new GuideOutputDTO();
        when(mapper.toDto(g1)).thenReturn(o1);

        Page<GuideOutputDTO> page = service.listGuides(q, pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(o1, page.getContent().get(0));
        verify(guideRepository).findByCategory(cat, pageable);
        verify(guideRepository, never()).findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(any(), any(), any());
    }

    @Test
    @DisplayName("listGuides: q no es Category → busca por título o contenido")
    void list_byText() {
        Pageable pageable = PageRequest.of(0, 10);
        String q = "rutina";

        Guide g1 = aGuide(1L);
        when(guideRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q, pageable))
                .thenReturn(new PageImpl<>(List.of(g1)));
        GuideOutputDTO o1 = new GuideOutputDTO();
        when(mapper.toDto(g1)).thenReturn(o1);

        Page<GuideOutputDTO> page = service.listGuides(q, pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(o1, page.getContent().get(0));
        verify(guideRepository).findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q, pageable);
    }

    // ---------- findByHabitId ----------
    @Test
    @DisplayName("findByHabitId: retorna guías mapeadas")
    void findByHabitId_ok() {
        Long habitId = 9L;
        Guide g = aGuide(100L);
        GuideOutputDTO o = new GuideOutputDTO();

        when(guideRepository.findByRecommendedHabits_Id(habitId)).thenReturn(List.of(g));
        when(mapper.toDto(g)).thenReturn(o);

        List<GuideOutputDTO> res = service.findByHabitId(habitId);

        assertEquals(1, res.size());
        assertSame(o, res.get(0));
        verify(guideRepository).findByRecommendedHabits_Id(habitId);
        verify(mapper).toDto(g);
    }
}