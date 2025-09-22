package una.ac.cr.FitFlow.service.Guide;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import una.ac.cr.FitFlow.dto.Guide.GuideInputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForGuide;
import una.ac.cr.FitFlow.model.Guide;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.repository.GuideRepository;
import una.ac.cr.FitFlow.repository.HabitRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // ← evita UnnecessaryStubbingException
class GuideServiceImplementationTest {

    @Mock private GuideRepository guideRepository;
    @Mock private HabitRepository habitRepository;
    @Mock private MapperForGuide mapper;

    @InjectMocks
    private GuideServiceImplementation service;

    private GuideInputDTO input(String title, String content, String category, Set<Long> recIds) {
        GuideInputDTO in = mock(GuideInputDTO.class);
        // Usamos lenient() para que los stubs no usados NO disparen UnnecessaryStubbingException
        lenient().when(in.getTitle()).thenReturn(title);
        lenient().when(in.getContent()).thenReturn(content);
        lenient().when(in.getCategory()).thenReturn(category);
        lenient().when(in.getRecommendedHabitIds()).thenReturn(recIds);
        return in;
    }

    /* ================== CREATE ================== */

    @Test
    @DisplayName("createGuide: feliz, con recommendedHabitIds")
    void create_ok_withHabits() {
        Set<Long> ids = Set.of(1L, 2L);
        GuideInputDTO in = input("t", "c", "PHYSICAL", ids);

        Guide entity = new Guide();
        when(mapper.toEntity(in)).thenReturn(entity);

        Habit h1 = Habit.builder().id(1L).build();
        Habit h2 = Habit.builder().id(2L).build();
        when(habitRepository.findAllById(ids)).thenReturn(List.of(h1, h2));

        Guide saved = new Guide();
        when(guideRepository.save(entity)).thenReturn(saved);

        GuideOutputDTO outDto = mock(GuideOutputDTO.class);
        when(mapper.toDto(saved)).thenReturn(outDto);

        GuideOutputDTO out = service.createGuide(in);

        assertThat(out).isSameAs(outDto);
        assertThat(entity.getRecommendedHabits()).containsExactlyInAnyOrder(h1, h2);
        verify(guideRepository).save(entity);
    }

    @Test
    @DisplayName("createGuide: recommendedHabitIds == null → no tocar (no consulta hábitos)")
    void create_ok_nullHabits() {
        GuideInputDTO in = input("t", "c", "MENTAL", null);

        Guide entity = new Guide();
        when(mapper.toEntity(in)).thenReturn(entity);

        Guide saved = new Guide();
        when(guideRepository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(mock(GuideOutputDTO.class));

        service.createGuide(in);

        verifyNoInteractions(habitRepository);
        verify(guideRepository).save(entity);
    }

    @Test
    @DisplayName("createGuide: recommendedHabitIds vacío → set vacío")
    void create_ok_emptyHabits() {
        GuideInputDTO in = input("t", "c", "SLEEP", Set.of());

        Guide entity = new Guide();
        when(mapper.toEntity(in)).thenReturn(entity);

        Guide saved = new Guide();
        when(guideRepository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(mock(GuideOutputDTO.class));

        service.createGuide(in);

        assertThat(entity.getRecommendedHabits()).isEmpty();
        verify(habitRepository, never()).findAllById(anySet());
    }

    @Test
    @DisplayName("createGuide: falla si falta título/contenido/categoría")
    void create_required_fields_fail() {
        assertThatThrownBy(() -> service.createGuide(input(null, "c", "DIET", null)))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("título");
        assertThatThrownBy(() -> service.createGuide(input("t", "  ", "DIET", null)))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("contenido");
        assertThatThrownBy(() -> service.createGuide(input("t", "c", null, null)))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("categoría");
    }

    @Test
    @DisplayName("createGuide: falla si algún recommendedHabitId no existe")
    void create_fail_missing_habit() {
        Set<Long> ids = Set.of(1L, 2L);
        GuideInputDTO in = input("t", "c", "SLEEP", ids);

        when(mapper.toEntity(in)).thenReturn(new Guide());
        when(habitRepository.findAllById(ids)).thenReturn(List.of(Habit.builder().id(1L).build())); // 1 de 2

        assertThatThrownBy(() -> service.createGuide(in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("recommendedHabitIds no existen");
    }

    /* ================== UPDATE ================== */

    @Test
    @DisplayName("updateGuide: feliz, copia cambios y setea hábitos si vienen")
    void update_ok_withHabits() {
        Long id = 7L;
        Set<Long> ids = Set.of(10L, 20L);
        GuideInputDTO in = input("T", "C", "PHYSICAL", ids);

        Guide existing = new Guide();
        when(guideRepository.findById(id)).thenReturn(Optional.of(existing));

        doAnswer(inv -> {
            GuideInputDTO src = inv.getArgument(0);
            Guide target = inv.getArgument(1);
            target.setTitle(src.getTitle());
            target.setContent(src.getContent());
            target.setCategory(Guide.Category.valueOf(src.getCategory()));
            return null;
        }).when(mapper).copyToEntity(eq(in), eq(existing));

        Habit h1 = Habit.builder().id(10L).build();
        Habit h2 = Habit.builder().id(20L).build();
        when(habitRepository.findAllById(ids)).thenReturn(List.of(h1, h2));

        Guide saved = new Guide();
        when(guideRepository.save(existing)).thenReturn(saved);

        GuideOutputDTO outDto = mock(GuideOutputDTO.class);
        when(mapper.toDto(saved)).thenReturn(outDto);

        GuideOutputDTO out = service.updateGuide(id, in);

        assertThat(out).isSameAs(outDto);
        assertThat(existing.getTitle()).isEqualTo("T");
        assertThat(existing.getContent()).isEqualTo("C");
        assertThat(existing.getCategory()).isEqualTo(Guide.Category.PHYSICAL);
        assertThat(existing.getRecommendedHabits()).containsExactlyInAnyOrder(h1, h2);
        verify(guideRepository).save(existing);
    }

    @Test
    @DisplayName("updateGuide: si recommendedHabitIds == null, no toca el set de hábitos")
    void update_ok_nullHabits_no_touch() {
        Long id = 8L;
        GuideInputDTO in = input("TT", "CC", "MENTAL", null);

        Guide existing = new Guide();
        existing.setRecommendedHabits(
            new java.util.HashSet<>(List.of(Habit.builder().id(1L).build()))
        );

        when(guideRepository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(mapper).copyToEntity(in, existing);

        when(guideRepository.save(existing)).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(mock(GuideOutputDTO.class));

        service.updateGuide(id, in);

        verifyNoInteractions(habitRepository);
        assertThat(existing.getRecommendedHabits()).hasSize(1);
    }

    @Test
    @DisplayName("updateGuide: falla si no existe la guía")
    void update_not_found() {
        when(guideRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.updateGuide(99L, input("t", "c", "DIET", null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Guía no encontrada");
    }

    @Test
    @DisplayName("updateGuide: falla si algún habitId no existe")
    void update_fail_missing_habit() {
        Long id = 1L;
        Set<Long> ids = Set.of(1L, 2L);
        GuideInputDTO in = input("t", "c", "SLEEP", ids);

        when(guideRepository.findById(id)).thenReturn(Optional.of(new Guide()));
        when(habitRepository.findAllById(ids)).thenReturn(List.of(Habit.builder().id(1L).build())); // 1 de 2

        assertThatThrownBy(() -> service.updateGuide(id, in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("recommendedHabitIds no existen");
    }

    /* ================== DELETE / FIND BY ID ================== */

    @Test
    @DisplayName("deleteGuide: feliz")
    void delete_ok() {
        Long id = 5L;
        Guide g = new Guide();
        when(guideRepository.findById(id)).thenReturn(Optional.of(g));

        service.deleteGuide(id);

        verify(guideRepository).delete(g);
    }

    @Test
    @DisplayName("deleteGuide: not found")
    void delete_not_found() {
        when(guideRepository.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteGuide(5L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Guía no encontrada");
    }

    @Test
    @DisplayName("findGuideById: feliz")
    void findById_ok() {
        Long id = 3L;
        Guide g = new Guide();
        when(guideRepository.findById(id)).thenReturn(Optional.of(g));

        GuideOutputDTO dto = mock(GuideOutputDTO.class);
        when(mapper.toDto(g)).thenReturn(dto);

        GuideOutputDTO out = service.findGuideById(id);
        assertThat(out).isSameAs(dto);
    }

    @Test
    @DisplayName("findGuideById: not found")
    void findById_not_found() {
        when(guideRepository.findById(3L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findGuideById(3L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Guía no encontrada");
    }

    /* ================== LIST ================== */

    @Test
    @DisplayName("listGuides: q == null → findAll")
    void list_null_q() {
        var pageable = PageRequest.of(0, 2);
        Guide g1 = new Guide();
        Guide g2 = new Guide();
        when(guideRepository.findAll(pageable)).thenReturn(
            new PageImpl<>(List.of(g1, g2), pageable, 2)
        );

        GuideOutputDTO d1 = mock(GuideOutputDTO.class);
        GuideOutputDTO d2 = mock(GuideOutputDTO.class);
        when(mapper.toDto(g1)).thenReturn(d1);
        when(mapper.toDto(g2)).thenReturn(d2);

        Page<GuideOutputDTO> out = service.listGuides(null, pageable);

        assertThat(out.getContent()).containsExactly(d1, d2);
        assertThat(out.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("listGuides: q en blanco → findAll")
    void list_blank_q() {
        var pageable = PageRequest.of(1, 3);
        Guide g = new Guide();
        when(guideRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(List.of(g), pageable, 1));
        when(mapper.toDto(g)).thenReturn(mock(GuideOutputDTO.class));

        Page<GuideOutputDTO> out = service.listGuides("   ", pageable);
        assertThat(out.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("listGuides: q coincide con categoría → findByCategory")
    void list_category_q() {
        var pageable = PageRequest.of(0, 5);
        Guide g = new Guide();
        when(guideRepository.findByCategory(Guide.Category.PHYSICAL, pageable))
            .thenReturn(new PageImpl<>(List.of(g), pageable, 1));
        when(mapper.toDto(g)).thenReturn(mock(GuideOutputDTO.class));

        Page<GuideOutputDTO> out = service.listGuides("physical", pageable);

        assertThat(out.getTotalElements()).isEqualTo(1);
        verify(guideRepository).findByCategory(Guide.Category.PHYSICAL, pageable);
        verify(guideRepository, never()).findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("listGuides: q no es categoría → busca por título o contenido (case-insensitive)")
    void list_text_q() {
        var pageable = PageRequest.of(2, 4);
        Guide g1 = new Guide();
        when(guideRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase("agua", "agua", pageable))
            .thenReturn(new PageImpl<>(List.of(g1), pageable, 1));
        when(mapper.toDto(g1)).thenReturn(mock(GuideOutputDTO.class));

        Page<GuideOutputDTO> out = service.listGuides("agua", pageable);

        assertThat(out.getTotalElements()).isEqualTo(1);
        verify(guideRepository).findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase("agua", "agua", pageable);
    }

    /* ================== BY HABIT ================== */

    @Test
    @DisplayName("findByHabitId: mapea la lista de entidades a DTOs")
    void findByHabitId_ok() {
        Long habitId = 77L;
        Guide g1 = new Guide();
        Guide g2 = new Guide();

        when(guideRepository.findByRecommendedHabits_Id(habitId)).thenReturn(List.of(g1, g2));

        GuideOutputDTO d1 = mock(GuideOutputDTO.class);
        GuideOutputDTO d2 = mock(GuideOutputDTO.class);
        when(mapper.toDto(g1)).thenReturn(d1);
        when(mapper.toDto(g2)).thenReturn(d2);

        List<GuideOutputDTO> out = service.findByHabitId(habitId);

        assertThat(out).containsExactly(d1, d2);
    }
}
