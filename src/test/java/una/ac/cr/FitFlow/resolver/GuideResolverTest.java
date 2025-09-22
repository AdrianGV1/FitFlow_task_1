package una.ac.cr.FitFlow.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.Guide.GuideInputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuidePageDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.Guide.GuideService;
import una.ac.cr.FitFlow.service.Habit.HabitService;

@ExtendWith(MockitoExtension.class)
class GuideResolverTest {

    @Mock private GuideService guideService;
    @Mock private HabitService habitService;

    private GuideResolver newResolver() {
        return new GuideResolver(guideService, habitService);
    }

    // ---------- Queries ----------

    @Test
    @DisplayName("guideById: invoca requireRead y delega a GuideService")
    void guideById_ok() {
        GuideResolver resolver = newResolver();
        Long id = 11L;
        GuideOutputDTO dto = mock(GuideOutputDTO.class);
        when(guideService.findGuideById(id)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            GuideOutputDTO out = resolver.guideById(id);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.GUIAS));
            verify(guideService).findGuideById(id);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("guides: pagina y mapea correctamente a GuidePageDTO")
    void guides_ok() {
        GuideResolver resolver = newResolver();
        int page = 2, size = 4;
        String keyword = "mindfulness";

        List<GuideOutputDTO> content = List.of(mock(GuideOutputDTO.class), mock(GuideOutputDTO.class));
        Page<GuideOutputDTO> returned =
                new PageImpl<>(content, Pageable.ofSize(size).withPage(page), 10);

        when(guideService.listGuides(eq(keyword), any(Pageable.class))).thenReturn(returned);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            GuidePageDTO out = resolver.guides(page, size, keyword);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.GUIAS));
            verify(guideService).listGuides(eq(keyword), eq(Pageable.ofSize(size).withPage(page)));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(10);
            assertThat(out.getTotalPages()).isEqualTo((int) Math.ceil(10.0 / size));
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    // ---------- Mutations ----------

    @Test
    @DisplayName("createGuide: requireWrite y delega al service")
    void createGuide_ok() {
        GuideResolver resolver = newResolver();
        GuideInputDTO input = mock(GuideInputDTO.class);
        GuideOutputDTO created = mock(GuideOutputDTO.class);
        when(guideService.createGuide(input)).thenReturn(created);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            GuideOutputDTO out = resolver.createGuide(input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.GUIAS));
            verify(guideService).createGuide(input);
            assertThat(out).isSameAs(created);
        }
    }

    @Test
    @DisplayName("updateGuide: requireWrite y delega al service")
    void updateGuide_ok() {
        GuideResolver resolver = newResolver();
        Long id = 5L;
        GuideInputDTO input = mock(GuideInputDTO.class);
        GuideOutputDTO updated = mock(GuideOutputDTO.class);
        when(guideService.updateGuide(id, input)).thenReturn(updated);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            GuideOutputDTO out = resolver.updateGuide(id, input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.GUIAS));
            verify(guideService).updateGuide(id, input);
            assertThat(out).isSameAs(updated);
        }
    }

    @Test
    @DisplayName("deleteGuide: requireWrite, llama al service y retorna true")
    void deleteGuide_ok() {
        GuideResolver resolver = newResolver();
        Long id = 77L;

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean out = resolver.deleteGuide(id);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.GUIAS));
            verify(guideService).deleteGuide(id);
            assertThat(out).isTrue();
        }
    }

    // ---------- SchemaMapping recommendedHabits ----------

    @Test
    @DisplayName("recommendedHabits: devuelve lista vacía si no hay IDs")
    void recommendedHabits_empty_ok() {
        GuideResolver resolver = newResolver();
        GuideOutputDTO guide = mock(GuideOutputDTO.class);
        when(guide.getRecommendedHabitIds()).thenReturn(null);

        List<HabitOutputDTO> out = resolver.recommendedHabits(guide);

        assertThat(out).isEmpty();
        verifyNoInteractions(habitService);
    }

    @Test
    @DisplayName("recommendedHabits: mapea IDs -> HabitOutputDTO con HabitService (orden indiferente)")
    void recommendedHabits_mapping_ok() {
        GuideResolver resolver = newResolver();
        GuideOutputDTO guide = mock(GuideOutputDTO.class);
        when(guide.getRecommendedHabitIds()).thenReturn(Set.of(1L, 2L, 3L));

        HabitOutputDTO h1 = mock(HabitOutputDTO.class);
        HabitOutputDTO h2 = mock(HabitOutputDTO.class);
        HabitOutputDTO h3 = mock(HabitOutputDTO.class);

        when(habitService.findHabitById(1L)).thenReturn(h1);
        when(habitService.findHabitById(2L)).thenReturn(h2);
        when(habitService.findHabitById(3L)).thenReturn(h3);

        List<HabitOutputDTO> out = resolver.recommendedHabits(guide);

        // Como el Set no garantiza orden, validamos en cualquier orden
        assertThat(out).containsExactlyInAnyOrder(h1, h2, h3);

        // Verificar que se consultaron los 3 IDs (sin importar el orden)
        verify(habitService).findHabitById(1L);
        verify(habitService).findHabitById(2L);
        verify(habitService).findHabitById(3L);
    }
}
