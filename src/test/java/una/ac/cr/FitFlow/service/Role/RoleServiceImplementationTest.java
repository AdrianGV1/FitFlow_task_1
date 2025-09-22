package una.ac.cr.FitFlow.service.Role;

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

import una.ac.cr.FitFlow.dto.Role.RoleInputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForRole;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.model.Role.Module;
import una.ac.cr.FitFlow.model.Role.Permission;
import una.ac.cr.FitFlow.repository.RoleRepository;
import una.ac.cr.FitFlow.service.role.RoleServiceImplementation;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplementationTest {

    @Mock private RoleRepository roleRepository;
    @Mock private MapperForRole mapper;

    @InjectMocks
    private RoleServiceImplementation service;

    private RoleInputDTO input(Long id, Module module, Permission perm) {
        RoleInputDTO dto = mock(RoleInputDTO.class);
        when(dto.getId()).thenReturn(id);
        when(dto.getModule()).thenReturn(module);
        when(dto.getPermissions()).thenReturn(perm);
        return dto;
    }

    /* ================== CREATE ================== */

    @Test
    @DisplayName("create: feliz")
    void create_ok() {
        RoleInputDTO in = input(null, Module.GUIAS, Permission.EDITOR);

        when(roleRepository.existsByModuleAndPermission(Module.GUIAS, Permission.EDITOR)).thenReturn(false);

        Role toSave = Role.builder().module(Module.GUIAS).permission(Permission.EDITOR).build();
        when(mapper.toEntity(in)).thenReturn(toSave);

        Role saved = Role.builder().id(10L).module(Module.GUIAS).permission(Permission.EDITOR).build();
        when(roleRepository.save(toSave)).thenReturn(saved);

        RoleOutputDTO out = service.create(in);

        assertThat(out.getId()).isEqualTo(10L);
        assertThat(out.getModule()).isEqualTo(Module.GUIAS);
        assertThat(out.getPermissions()).isEqualTo(Permission.EDITOR);
        assertThat(out.getName()).isEqualTo("GUIAS_EDITOR"); // deriveName
    }

    @Test
    @DisplayName("create: falla si (module, permission) ya existe")
    void create_duplicate() {
        RoleInputDTO in = input(null, Module.RUTINAS, Permission.AUDITOR);
        when(roleRepository.existsByModuleAndPermission(Module.RUTINAS, Permission.AUDITOR)).thenReturn(true);

        assertThatThrownBy(() -> service.create(in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Ya existe un role");
        verify(roleRepository, never()).save(any());
    }

    /* ================== UPDATE ================== */

    @Test
    @DisplayName("update: feliz sin cambiar module/permission (usa los actuales)")
    void update_ok_no_change() {
        Long id = 5L;
        RoleInputDTO in = input(id, null, null); // no trae cambios

        Role existing = Role.builder().id(id).module(Module.ACTIVIDADES).permission(Permission.EDITOR).build();
        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));

        // No conflicto con otros
        when(roleRepository.existsByModuleAndPermissionAndIdNot(Module.ACTIVIDADES, Permission.EDITOR, id))
            .thenReturn(false);

        Role saved = Role.builder().id(id).module(Module.ACTIVIDADES).permission(Permission.EDITOR).build();
        when(roleRepository.save(existing)).thenReturn(saved);

        RoleOutputDTO out = service.update(in);

        assertThat(out.getId()).isEqualTo(id);
        assertThat(out.getModule()).isEqualTo(Module.ACTIVIDADES);
        assertThat(out.getPermissions()).isEqualTo(Permission.EDITOR);
        assertThat(out.getName()).isEqualTo("ACTIVIDADES_EDITOR");
    }

    @Test
    @DisplayName("update: feliz cambiando module/permission")
    void update_ok_change_both() {
        Long id = 7L;
        RoleInputDTO in = input(id, Module.PROGRESO, Permission.AUDITOR);

        Role existing = Role.builder().id(id).module(Module.GUIAS).permission(Permission.EDITOR).build();
        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));

        when(roleRepository.existsByModuleAndPermissionAndIdNot(Module.PROGRESO, Permission.AUDITOR, id))
            .thenReturn(false);

        // El servicio modifica la entidad y salva
        Role saved = Role.builder().id(id).module(Module.PROGRESO).permission(Permission.AUDITOR).build();
        when(roleRepository.save(any(Role.class))).thenReturn(saved);

        RoleOutputDTO out = service.update(in);

        assertThat(out.getId()).isEqualTo(id);
        assertThat(out.getModule()).isEqualTo(Module.PROGRESO);
        assertThat(out.getPermissions()).isEqualTo(Permission.AUDITOR);
        assertThat(out.getName()).isEqualTo("PROGRESO_AUDITOR");
    }

    @Test
    @DisplayName("update: falla si id es null")
    void update_id_null() {
        RoleInputDTO in = input(null, Module.GUIAS, Permission.EDITOR);
        assertThatThrownBy(() -> service.update(in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("id es obligatorio");
    }

    @Test
    @DisplayName("update: falla si role no existe")
    void update_not_found() {
        RoleInputDTO in = input(9L, Module.GUIAS, Permission.EDITOR);
        when(roleRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Role no encontrado");
    }

    @Test
    @DisplayName("update: falla si otro role ya usa (module, permission)")
    void update_conflict_other() {
        Long id = 4L;
        RoleInputDTO in = input(id, Module.RECORDATORIOS, Permission.AUDITOR);

        Role existing = Role.builder().id(id).module(Module.RECORDATORIOS).permission(Permission.EDITOR).build();
        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));

        when(roleRepository.existsByModuleAndPermissionAndIdNot(Module.RECORDATORIOS, Permission.AUDITOR, id))
            .thenReturn(true);

        assertThatThrownBy(() -> service.update(in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Otro role ya usa ese");
        verify(roleRepository, never()).save(any());
    }

    /* ================== DELETE ================== */

    @Test
    @DisplayName("delete: feliz")
    void delete_ok() {
        Long id = 12L;
        Role existing = Role.builder().id(id).build();
        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));

        service.delete(id);

        verify(roleRepository).delete(existing);
    }

    @Test
    @DisplayName("delete: role no existe")
    void delete_not_found() {
        when(roleRepository.findById(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(100L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no existe");
        verify(roleRepository, never()).delete(any());
    }

    /* ================== FIND & LIST ================== */

    @Test
    @DisplayName("findById: feliz y nombre derivado")
    void findById_ok() {
        Long id = 3L;
        Role role = Role.builder().id(id).module(Module.GUIAS).permission(Permission.AUDITOR).build();
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        RoleOutputDTO out = service.findById(id);

        assertThat(out.getId()).isEqualTo(3L);
        assertThat(out.getModule()).isEqualTo(Module.GUIAS);
        assertThat(out.getPermissions()).isEqualTo(Permission.AUDITOR);
        assertThat(out.getName()).isEqualTo("GUIAS_AUDITOR");
    }

    @Test
    @DisplayName("findById: not found")
    void findById_not_found() {
        when(roleRepository.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(9L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Role no encontrado");
    }

    @Test
    @DisplayName("listRoles: mapea con deriveName")
    void listRoles_ok() {
        var pageable = PageRequest.of(0, 2);

        Role r1 = Role.builder().id(1L).module(Module.ACTIVIDADES).permission(Permission.EDITOR).build();
        Role r2 = Role.builder().id(2L).module(Module.PROGRESO).permission(Permission.AUDITOR).build();

        Page<Role> page = new PageImpl<>(List.of(r1, r2), pageable, 2);
        when(roleRepository.findAll(pageable)).thenReturn(page);

        Page<RoleOutputDTO> out = service.listRoles(pageable);

        assertThat(out.getContent()).extracting(RoleOutputDTO::getId).containsExactly(1L, 2L);
        assertThat(out.getContent()).extracting(RoleOutputDTO::getName)
            .containsExactly("ACTIVIDADES_EDITOR", "PROGRESO_AUDITOR");
        assertThat(out.getContent()).extracting(RoleOutputDTO::getModule)
            .containsExactly(Module.ACTIVIDADES, Module.PROGRESO);
        assertThat(out.getContent()).extracting(RoleOutputDTO::getPermissions)
            .containsExactly(Permission.EDITOR, Permission.AUDITOR);
    }
}
