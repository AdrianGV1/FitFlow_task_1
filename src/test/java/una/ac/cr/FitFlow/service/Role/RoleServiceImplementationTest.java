package una.ac.cr.FitFlow.service.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.Role.RoleInputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForRole;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.repository.RoleRepository;
import una.ac.cr.FitFlow.service.role.RoleServiceImplementation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplementationTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MapperForRole mapper;

    @InjectMocks
    private RoleServiceImplementation roleService;

    private Role mockRole;
    private RoleInputDTO mockInputDTO;
    private RoleOutputDTO mockOutputDTO;

    @BeforeEach
    void setUp() {
        // Setup mock objects según el modelo actual
        mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setModule(Role.Module.RUTINAS);
        mockRole.setPermission(Role.Permission.EDITOR);

        mockInputDTO = new RoleInputDTO();
        mockInputDTO.setId(1L);
        mockInputDTO.setModule(Role.Module.RUTINAS);
        mockInputDTO.setPermissions(Role.Permission.EDITOR);

        mockOutputDTO = RoleOutputDTO.builder()
                .id(1L)
                .name("RUTINAS_EDITOR")
                .module(Role.Module.RUTINAS)
                .permissions(Role.Permission.EDITOR)
                .build();
    }

    // Tests for create method
    @Test
    @DisplayName("create should create role successfully with valid input")
    void create_WithValidInput_ShouldCreateSuccessfully() {
        // Given
        when(roleRepository.existsByModuleAndPermission(Role.Module.RUTINAS, Role.Permission.EDITOR))
                .thenReturn(false);
        when(mapper.toEntity(mockInputDTO)).thenReturn(mockRole);
        when(roleRepository.save(mockRole)).thenReturn(mockRole);

        // When
        RoleOutputDTO result = roleService.create(mockInputDTO);

        // Then
        assertNotNull(result);
        assertEquals("RUTINAS_EDITOR", result.getName());
        assertEquals(Role.Module.RUTINAS, result.getModule());
        assertEquals(Role.Permission.EDITOR, result.getPermissions());
        
        verify(roleRepository).existsByModuleAndPermission(Role.Module.RUTINAS, Role.Permission.EDITOR);
        verify(roleRepository).save(mockRole);
    }

    @Test
    @DisplayName("create should throw exception when role with same module and permission exists")
    void create_WithExistingModuleAndPermission_ShouldThrowException() {
        // Given
        when(roleRepository.existsByModuleAndPermission(Role.Module.RUTINAS, Role.Permission.EDITOR))
                .thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleService.create(mockInputDTO)
        );
        assertEquals("Ya existe un role con ese (module, permission).", exception.getMessage());
        
        verify(roleRepository).existsByModuleAndPermission(Role.Module.RUTINAS, Role.Permission.EDITOR);
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("create should derive correct name from module and permission")
    void create_ShouldDeriveCorrectName() {
        // Given
        mockInputDTO.setModule(Role.Module.ACTIVIDADES);
        mockInputDTO.setPermissions(Role.Permission.AUDITOR);
        
        mockRole.setModule(Role.Module.ACTIVIDADES);
        mockRole.setPermission(Role.Permission.AUDITOR);

        when(roleRepository.existsByModuleAndPermission(Role.Module.ACTIVIDADES, Role.Permission.AUDITOR))
                .thenReturn(false);
        when(mapper.toEntity(mockInputDTO)).thenReturn(mockRole);
        when(roleRepository.save(mockRole)).thenReturn(mockRole);

        // When
        RoleOutputDTO result = roleService.create(mockInputDTO);

        // Then
        assertEquals("ACTIVIDADES_AUDITOR", result.getName());
    }

    // Tests for update method
    @Test
    @DisplayName("update should update role successfully with valid input")
    void update_WithValidInput_ShouldUpdateSuccessfully() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        when(roleRepository.existsByModuleAndPermissionAndIdNot(Role.Module.RUTINAS, Role.Permission.EDITOR, 1L))
                .thenReturn(false);
        when(roleRepository.save(mockRole)).thenReturn(mockRole);

        // When
        RoleOutputDTO result = roleService.update(mockInputDTO);

        // Then
        assertNotNull(result);
        assertEquals("RUTINAS_EDITOR", result.getName());
        
        verify(roleRepository).findById(1L);
        verify(roleRepository).existsByModuleAndPermissionAndIdNot(Role.Module.RUTINAS, Role.Permission.EDITOR, 1L);
        verify(roleRepository).save(mockRole);
    }

    @Test
    @DisplayName("update should throw exception when id is null")
    void update_WithNullId_ShouldThrowException() {
        // Given
        mockInputDTO.setId(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleService.update(mockInputDTO)
        );
        assertEquals("El id es obligatorio para actualizar.", exception.getMessage());
        
        verify(roleRepository, never()).findById(any());
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should throw exception when role not found")
    void update_WithNonExistentRole_ShouldThrowException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleService.update(mockInputDTO)
        );
        assertEquals("Role no encontrado: id=1", exception.getMessage());
        
        verify(roleRepository).findById(1L);
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should throw exception when module and permission combination already exists")
    void update_WithExistingModuleAndPermission_ShouldThrowException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        when(roleRepository.existsByModuleAndPermissionAndIdNot(Role.Module.RUTINAS, Role.Permission.EDITOR, 1L))
                .thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleService.update(mockInputDTO)
        );
        assertEquals("Otro role ya usa ese (module, permission).", exception.getMessage());
        
        verify(roleRepository).findById(1L);
        verify(roleRepository).existsByModuleAndPermissionAndIdNot(Role.Module.RUTINAS, Role.Permission.EDITOR, 1L);
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should use existing values when module is null")
    void update_WithNullModule_ShouldUseExistingModule() {
        // Given
        mockInputDTO.setModule(null);
        mockInputDTO.setPermissions(Role.Permission.AUDITOR);
        
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        when(roleRepository.existsByModuleAndPermissionAndIdNot(Role.Module.RUTINAS, Role.Permission.AUDITOR, 1L))
                .thenReturn(false);
        when(roleRepository.save(mockRole)).thenReturn(mockRole);

        // When
        RoleOutputDTO result = roleService.update(mockInputDTO);

        // Then
        assertNotNull(result);
        assertEquals("RUTINAS_AUDITOR", result.getName());
        assertEquals(Role.Permission.AUDITOR, result.getPermissions());
    }

    @Test
    @DisplayName("update should use existing values when permission is null")
    void update_WithNullPermission_ShouldUseExistingPermission() {
        // Given
        mockInputDTO.setModule(Role.Module.ACTIVIDADES);
        mockInputDTO.setPermissions(null);
        
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        when(roleRepository.existsByModuleAndPermissionAndIdNot(Role.Module.ACTIVIDADES, Role.Permission.EDITOR, 1L))
                .thenReturn(false);
        when(roleRepository.save(mockRole)).thenReturn(mockRole);

        // When
        RoleOutputDTO result = roleService.update(mockInputDTO);

        // Then
        assertNotNull(result);
        assertEquals("ACTIVIDADES_EDITOR", result.getName());
        assertEquals(Role.Module.ACTIVIDADES, result.getModule());
    }

    @Test
    @DisplayName("update should use existing values when both module and permission are null")
    void update_WithNullModuleAndPermission_ShouldUseExistingValues() {
        // Given
        mockInputDTO.setModule(null);
        mockInputDTO.setPermissions(null);
        
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        when(roleRepository.existsByModuleAndPermissionAndIdNot(Role.Module.RUTINAS, Role.Permission.EDITOR, 1L))
                .thenReturn(false);
        when(roleRepository.save(mockRole)).thenReturn(mockRole);

        // When
        RoleOutputDTO result = roleService.update(mockInputDTO);

        // Then
        assertNotNull(result);
        assertEquals("RUTINAS_EDITOR", result.getName()); // Should keep original values
    }

    // Tests for delete method
    @Test
    @DisplayName("delete should delete role successfully when it exists")
    void delete_WithExistingRole_ShouldDeleteSuccessfully() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));

        // When
        roleService.delete(1L);

        // Then
        verify(roleRepository).findById(1L);
        verify(roleRepository).delete(mockRole);
    }

    @Test
    @DisplayName("delete should throw exception when role doesn't exist")
    void delete_WithNonExistentRole_ShouldThrowException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleService.delete(1L)
        );
        assertEquals("El role con id=1 no existe.", exception.getMessage());
        
        verify(roleRepository).findById(1L);
        verify(roleRepository, never()).delete(any());
    }

    // Tests for findById method
    @Test
    @DisplayName("findById should return role when it exists")
    void findById_WithExistingRole_ShouldReturnRole() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));

        // When
        RoleOutputDTO result = roleService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals("RUTINAS_EDITOR", result.getName());
        assertEquals(Role.Module.RUTINAS, result.getModule());
        assertEquals(Role.Permission.EDITOR, result.getPermissions());
        
        verify(roleRepository).findById(1L);
    }

    @Test
    @DisplayName("findById should throw exception when role doesn't exist")
    void findById_WithNonExistentRole_ShouldThrowException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleService.findById(1L)
        );
        assertEquals("Role no encontrado: id=1", exception.getMessage());
        
        verify(roleRepository).findById(1L);
    }

    // Tests for listRoles method
    @Test
    @DisplayName("listRoles should return paginated roles")
    void listRoles_ShouldReturnPaginatedRoles() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> rolePage = new PageImpl<>(List.of(mockRole));
        
        when(roleRepository.findAll(any(Pageable.class))).thenReturn(rolePage);

        // When
        Page<RoleOutputDTO> result = roleService.listRoles(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        RoleOutputDTO dto = result.getContent().get(0);
        assertEquals("RUTINAS_EDITOR", dto.getName());
        assertEquals(Role.Module.RUTINAS, dto.getModule());
        assertEquals(Role.Permission.EDITOR, dto.getPermissions());
        
        verify(roleRepository).findAll(pageable);
    }

    @Test
    @DisplayName("listRoles should return empty page when no roles exist")
    void listRoles_WithNoRoles_ShouldReturnEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> emptyPage = new PageImpl<>(List.of());
        
        when(roleRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When
        Page<RoleOutputDTO> result = roleService.listRoles(pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        
        verify(roleRepository).findAll(pageable);
    }

    // Tests for all module and permission combinations based on actual model
    @Test
    @DisplayName("create should handle all module and permission combinations correctly")
    void create_WithAllCombinations_ShouldHandleCorrectly() {
        // Test all module combinations with actual enums from the model
        testModulePermissionCombination(Role.Module.RUTINAS, Role.Permission.EDITOR, "RUTINAS_EDITOR");
        testModulePermissionCombination(Role.Module.RUTINAS, Role.Permission.AUDITOR, "RUTINAS_AUDITOR");
        
        testModulePermissionCombination(Role.Module.ACTIVIDADES, Role.Permission.EDITOR, "ACTIVIDADES_EDITOR");
        testModulePermissionCombination(Role.Module.ACTIVIDADES, Role.Permission.AUDITOR, "ACTIVIDADES_AUDITOR");
        
        testModulePermissionCombination(Role.Module.GUIAS, Role.Permission.EDITOR, "GUIAS_EDITOR");
        testModulePermissionCombination(Role.Module.GUIAS, Role.Permission.AUDITOR, "GUIAS_AUDITOR");
        
        testModulePermissionCombination(Role.Module.PROGRESO, Role.Permission.EDITOR, "PROGRESO_EDITOR");
        testModulePermissionCombination(Role.Module.PROGRESO, Role.Permission.AUDITOR, "PROGRESO_AUDITOR");
        
        testModulePermissionCombination(Role.Module.RECORDATORIOS, Role.Permission.EDITOR, "RECORDATORIOS_EDITOR");
        testModulePermissionCombination(Role.Module.RECORDATORIOS, Role.Permission.AUDITOR, "RECORDATORIOS_AUDITOR");
    }

    private void testModulePermissionCombination(Role.Module module, Role.Permission permission, String expectedName) {
        RoleInputDTO dto = new RoleInputDTO();
        dto.setModule(module);
        dto.setPermissions(permission);

        Role role = new Role();
        role.setModule(module);
        role.setPermission(permission);

        when(roleRepository.existsByModuleAndPermission(module, permission)).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);

        RoleOutputDTO result = roleService.create(dto);
        assertEquals(expectedName, result.getName());

        // Reset mocks for next iteration
        reset(roleRepository, mapper);
    }

    // Edge case tests
    @Test
    @DisplayName("deriveName should handle enum name changes correctly")
    void deriveName_ShouldHandleEnumNamesCorrectly() {
        // This test verifies that the name derivation uses the actual enum names
        Role.Module[] modules = Role.Module.values();
        Role.Permission[] permissions = Role.Permission.values();
        
        for (Role.Module module : modules) {
            for (Role.Permission permission : permissions) {
                String expectedName = module.name() + "_" + permission.name();
                RoleInputDTO dto = new RoleInputDTO();
                dto.setModule(module);
                dto.setPermissions(permission);
                
                Role role = new Role();
                role.setModule(module);
                role.setPermission(permission);
                
                when(roleRepository.existsByModuleAndPermission(module, permission)).thenReturn(false);
                when(mapper.toEntity(dto)).thenReturn(role);
                when(roleRepository.save(role)).thenReturn(role);
                
                RoleOutputDTO result = roleService.create(dto);
                assertEquals(expectedName, result.getName());
                
                reset(roleRepository, mapper);
            }
        }
    }

    @Test
    @DisplayName("update should handle case when only ID is provided")
    void update_WithOnlyId_ShouldUseExistingModuleAndPermission() {
        // Given - only set ID, leave module and permission as null
        RoleInputDTO dto = new RoleInputDTO();
        dto.setId(1L);
        // module and permission are null
        
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        when(roleRepository.existsByModuleAndPermissionAndIdNot(Role.Module.RUTINAS, Role.Permission.EDITOR, 1L))
                .thenReturn(false);
        when(roleRepository.save(mockRole)).thenReturn(mockRole);

        // When
        RoleOutputDTO result = roleService.update(dto);

        // Then - should keep original values
        assertEquals("RUTINAS_EDITOR", result.getName());
        assertEquals(Role.Module.RUTINAS, result.getModule());
        assertEquals(Role.Permission.EDITOR, result.getPermissions());
    }

    @Test
    @DisplayName("Service should handle role with users collection")
    void roleWithUsers_ShouldBeHandledCorrectly() {
        // Given - role has users (testing that the service doesn't break with the relationship)
        when(roleRepository.existsByModuleAndPermission(Role.Module.RUTINAS, Role.Permission.EDITOR))
                .thenReturn(false);
        when(mapper.toEntity(mockInputDTO)).thenReturn(mockRole);
        when(roleRepository.save(mockRole)).thenReturn(mockRole);

        // When - the role has users collection (from @ManyToMany relationship)
        RoleOutputDTO result = roleService.create(mockInputDTO);

        // Then - service should work normally despite the users relationship
        assertNotNull(result);
        assertEquals("RUTINAS_EDITOR", result.getName());
        
        // Verify that the users collection doesn't interfere with service operations
        verify(roleRepository).existsByModuleAndPermission(Role.Module.RUTINAS, Role.Permission.EDITOR);
        verify(roleRepository).save(mockRole);
    }
}