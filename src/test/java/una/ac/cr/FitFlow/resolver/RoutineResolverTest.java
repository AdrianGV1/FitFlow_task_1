package una.ac.cr.FitFlow.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

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
import org.springframework.data.domain.PageRequest;

import una.ac.cr.FitFlow.dto.Routine.RoutineInputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutinePageDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.Routine.RoutineService;
import una.ac.cr.FitFlow.service.RoutineActivity.RoutineActivityService;
import una.ac.cr.FitFlow.service.user.UserService;

@ExtendWith(MockitoExtension.class)
class RoutineResolverTest {

    @Mock private RoutineService routineService;
    @Mock private UserService userService;
    @Mock private RoutineActivityService routineActivityService;

    private RoutineResolver newResolver() {
        return new RoutineResolver(routineService, userService, routineActivityService);
    }

    // ================== Queries ==================

    @Test
    @DisplayName("routineById: requiere lectura y delega a RoutineService.findById")
    void routineById_ok() {
        RoutineResolver resolver = newResolver();
        Long id = 10L;
        RoutineOutputDTO dto = mock(RoutineOutputDTO.class);
        when(routineService.findById(id)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutineOutputDTO out = resolver.routineById(id);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RUTINAS));
            verify(routineService).findById(id);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("routines: pagina y mapea a RoutinePageDTO")
    void routines_ok() {
        RoutineResolver resolver = newResolver();
        int page = 1, size = 3;
        String keyword = "fit";

        List<RoutineOutputDTO> content = List.of(mock(RoutineOutputDTO.class), mock(RoutineOutputDTO.class));
        Page<RoutineOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 7);
        when(routineService.list(eq(keyword), eq(PageRequest.of(page, size)))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutinePageDTO out = resolver.routines(page, size, keyword);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RUTINAS));
            verify(routineService).list(eq(keyword), eq(PageRequest.of(page, size)));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(7L);
            assertThat(out.getTotalPages()).isEqualTo((int)Math.ceil(7.0/size));
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    @Test
    @DisplayName("routinesByUserId: pagina y mapea a RoutinePageDTO")
    void routinesByUserId_ok() {
        RoutineResolver resolver = newResolver();
        Long userId = 55L;
        int page = 0, size = 2;

        List<RoutineOutputDTO> content = List.of(mock(RoutineOutputDTO.class));
        Page<RoutineOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 1);
        when(routineService.listByUserId(userId, PageRequest.of(page, size))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutinePageDTO out = resolver.routinesByUserId(userId, page, size);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RUTINAS));
            verify(routineService).listByUserId(userId, PageRequest.of(page, size));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(1L);
            assertThat(out.getTotalPages()).isEqualTo(1);
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    // ================== Mutations ==================

    @Test
    @DisplayName("createRoutine: requiere escritura y delega a RoutineService.create")
    void createRoutine_ok() {
        RoutineResolver resolver = newResolver();
        RoutineInputDTO input = mock(RoutineInputDTO.class);
        RoutineOutputDTO created = mock(RoutineOutputDTO.class);
        when(routineService.create(input)).thenReturn(created);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutineOutputDTO out = resolver.createRoutine(input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RUTINAS));
            verify(routineService).create(input);
            assertThat(out).isSameAs(created);
        }
    }

    @Test
    @DisplayName("updateRoutine: requiere escritura y delega a RoutineService.update")
    void updateRoutine_ok() {
        RoutineResolver resolver = newResolver();
        Long id = 9L;
        RoutineInputDTO input = mock(RoutineInputDTO.class);
        RoutineOutputDTO updated = mock(RoutineOutputDTO.class);
        when(routineService.update(id, input)).thenReturn(updated);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoutineOutputDTO out = resolver.updateRoutine(id, input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RUTINAS));
            verify(routineService).update(id, input);
            assertThat(out).isSameAs(updated);
        }
    }

    @Test
    @DisplayName("deleteRoutine: requiere escritura, borra y retorna true")
    void deleteRoutine_ok() {
        RoutineResolver resolver = newResolver();
        Long id = 7L;

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean out = resolver.deleteRoutine(id);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RUTINAS));
            verify(routineService).delete(id);
            assertThat(out).isTrue();
        }
    }

    // ================== Schema mappings ==================

    @Test
    @DisplayName("@SchemaMapping user: delega a UserService.findUserById")
    void schema_user_ok() {
        RoutineResolver resolver = newResolver();
        RoutineOutputDTO routine = mock(RoutineOutputDTO.class);
        when(routine.getUserId()).thenReturn(42L);

        UserOutputDTO user = mock(UserOutputDTO.class);
        when(userService.findUserById(42L)).thenReturn(user);

        UserOutputDTO out = resolver.user(routine);

        verify(userService).findUserById(42L);
        assertThat(out).isSameAs(user);
    }

    @Test
    @DisplayName("@SchemaMapping activities: devuelve lista vacía si no hay IDs")
    void schema_activities_empty_ok() {
        RoutineResolver resolver = newResolver();
        RoutineOutputDTO routine = mock(RoutineOutputDTO.class);
        when(routine.getActivityIds()).thenReturn(null);

        List<RoutineActivityOutputDTO> out = resolver.activities(routine);

        assertThat(out).isEmpty();
        verifyNoInteractions(routineActivityService);
    }

    @Test
    @DisplayName("@SchemaMapping activities: mapea IDs -> RoutineActivityOutputDTO (orden indiferente)")
    void schema_activities_ok() {
        RoutineResolver resolver = newResolver();
        RoutineOutputDTO routine = mock(RoutineOutputDTO.class);

        // Si tu DTO usa Set<Long>, mejor usamos Set.of(...) para evitar confusiones de orden
        when(routine.getActivityIds()).thenReturn(List.of(1L, 2L, 3L));


        RoutineActivityOutputDTO a1 = mock(RoutineActivityOutputDTO.class);
        RoutineActivityOutputDTO a2 = mock(RoutineActivityOutputDTO.class);
        RoutineActivityOutputDTO a3 = mock(RoutineActivityOutputDTO.class);

        when(routineActivityService.findById(1L)).thenReturn(a1);
        when(routineActivityService.findById(2L)).thenReturn(a2);
        when(routineActivityService.findById(3L)).thenReturn(a3);

        List<RoutineActivityOutputDTO> out = resolver.activities(routine);

        // Como puede ser Set, el orden no está garantizado
        assertThat(out).containsExactlyInAnyOrder(a1, a2, a3);
        verify(routineActivityService).findById(1L);
        verify(routineActivityService).findById(2L);
        verify(routineActivityService).findById(3L);
    }
}
