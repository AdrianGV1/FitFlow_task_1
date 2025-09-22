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
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.Role.RoleInputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.dto.Role.RolePageDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.role.RoleService;

@ExtendWith(MockitoExtension.class)
class RoleResolverTest {

    @Mock
    private RoleService roleService;

    private RoleResolver newResolver() {
        return new RoleResolver(roleService);
    }

    @Test
    @DisplayName("roleById: requireRead y delega a RoleService.findById")
    void roleById_ok() {
        RoleResolver resolver = newResolver();
        Long id = 123L;
        RoleOutputDTO dto = mock(RoleOutputDTO.class);
        when(roleService.findById(id)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoleOutputDTO out = resolver.roleById(id);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RUTINAS));
            verify(roleService).findById(id);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("roles: pagina y mapea correctamente a RolePageDTO")
    void roles_ok() {
        RoleResolver resolver = newResolver();
        int page = 2, size = 4;

        List<RoleOutputDTO> content = List.of(mock(RoleOutputDTO.class), mock(RoleOutputDTO.class));
        // Ajustamos totalElements a 10 y relajamos el stub con any(Pageable.class)
        Page<RoleOutputDTO> p = new PageImpl<>(content, Pageable.ofSize(size).withPage(page), 10);
        when(roleService.listRoles(any(Pageable.class))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RolePageDTO out = resolver.roles(page, size);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RUTINAS));
            verify(roleService).listRoles(eq(Pageable.ofSize(size).withPage(page)));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(10L);
            assertThat(out.getTotalPages()).isEqualTo((int) Math.ceil(10.0 / size));
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    @Test
    @DisplayName("createRole: requireWrite y delega a RoleService.create")
    void createRole_ok() {
        RoleResolver resolver = newResolver();
        RoleInputDTO input = mock(RoleInputDTO.class);
        RoleOutputDTO created = mock(RoleOutputDTO.class);
        when(roleService.create(input)).thenReturn(created);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoleOutputDTO out = resolver.createRole(input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RUTINAS));
            verify(roleService).create(input);
            assertThat(out).isSameAs(created);
        }
    }

    @Test
    @DisplayName("updateRole: requireWrite y delega a RoleService.update")
    void updateRole_ok() {
        RoleResolver resolver = newResolver();
        RoleInputDTO input = mock(RoleInputDTO.class);
        RoleOutputDTO updated = mock(RoleOutputDTO.class);
        when(roleService.update(input)).thenReturn(updated);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            RoleOutputDTO out = resolver.updateRole(input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RUTINAS));
            verify(roleService).update(input);
            assertThat(out).isSameAs(updated);
        }
    }

    @Test
    @DisplayName("deleteRole: requireWrite, borra y retorna true")
    void deleteRole_ok() {
        RoleResolver resolver = newResolver();
        Long id = 77L;

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean out = resolver.deleteRole(id);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RUTINAS));
            verify(roleService).delete(id);
            assertThat(out).isTrue();
        }
    }
}
