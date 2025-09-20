package una.ac.cr.FitFlow.service.role;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.Role.RoleInputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.repository.RoleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImplementation implements RoleService {

    private final RoleRepository roleRepository;

    private RoleOutputDTO convertToDto(Role role) {
        return RoleOutputDTO.builder()
                .id(role.getId())
                .name(role.getModule().toString())
                .permissions(Set.of(role.getPermission().name()))
                .build();
    }

    private Role convertToEntity(final RoleInputDTO roleDto) {
        Role.Module module = parseModule(roleDto.getName());
        Role.Permission permission = parsePermission(singlePermissionFrom(roleDto.getPermissions()));
        return Role.builder()
                .module(module)
                .permission(permission)
                .build();
    }

    private Role.Module parseModule(String module) {
        if (module == null)
            throw new IllegalArgumentException("El nombre es obligatorio");
        try {
            return Role.Module.valueOf(module.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Módulo inválido: " + module);
        }
    }

    private Role.Permission parsePermission(String permission) {
        if (permission == null)
            throw new IllegalArgumentException("El permiso es obligatorio");
        try {
            return Role.Permission.valueOf(permission.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Permiso inválido: " + permission);
        }
    }

    private String singlePermissionFrom(Set<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            throw new IllegalArgumentException("Debe especificar exactamente un permiso");
        }
        if (permissions.size() > 1) {
            throw new IllegalArgumentException("Solo se admite un permiso por role");
        }
        return permissions.iterator().next();
    }

    @Override
    @Transactional
    public RoleOutputDTO create(RoleInputDTO roleDTO) {
        Role roleEntity = convertToEntity(roleDTO);
        boolean exists = roleRepository.existsByModuleAndPermission(roleEntity.getModule(), roleEntity.getPermission());
        if (exists) {
            throw new IllegalArgumentException("El modulo ya existe");
        }
        roleRepository.save(roleEntity);
        return convertToDto(roleEntity);
    }

    @Override
    @Transactional
    public RoleOutputDTO update(RoleInputDTO roleDTO) {
        if (roleDTO.getId() == null) {
            throw new IllegalArgumentException("El id es obligatorio");
        }
        Role existingRole = roleRepository.findById(roleDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Role no encontrado"));

        Role.Module module = parseModule(roleDTO.getName());
        Role.Permission permission = parsePermission(singlePermissionFrom(roleDTO.getPermissions()));

        boolean exists = roleRepository.existsByModuleAndPermissionAndIdNot(module, permission, roleDTO.getId());
        if (exists) {
            throw new IllegalArgumentException("El modulo ya existe");
        }

        existingRole.setModule(module);
        existingRole.setPermission(permission);
        roleRepository.save(existingRole);
        return convertToDto(existingRole);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("El role con el id: " + id + " no existe");
        }
    }

    @Override
    public RoleOutputDTO findById(Long id) {
        if (roleRepository.existsById(id)) {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Role no encontrado"));
            return convertToDto(role);
        } else {
            throw new IllegalArgumentException("El role con el id: " + id + " no existe");
        }
    }

    @Override
    public Page<RoleOutputDTO> listRoles(Pageable pageable) {
        Page<Role> roles = roleRepository.findAll(pageable);
        return roles.map(this::convertToDto);
    }
}

