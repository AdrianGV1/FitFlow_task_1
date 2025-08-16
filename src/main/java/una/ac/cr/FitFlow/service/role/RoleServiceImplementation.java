package una.ac.cr.FitFlow.service.role;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.RoleDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.repository.RoleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImplementation implements RoleService {

    private final RoleRepository roleRepository;

    private RoleDTO convertToDto(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getModule().toString())
                .permissions(Set.of(role.getPermission().name()))
                .build();
    }

    private Role convertToEntity(final RoleDTO roleDto) {
        Role.Module module = parseModule(roleDto.getName());
        Role.Permission permission = parsePermission(singlePermissionFromDto(roleDto));
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

    private String singlePermissionFromDto(RoleDTO dto) {
        if (dto.getPermissions() == null || dto.getPermissions().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar exactamente un permiso");
        }
        if (dto.getPermissions().size() > 1) {
            throw new IllegalArgumentException("Solo se admite un permiso por role");
        }
        return dto.getPermissions().iterator().next();
    }

    @Override
    @Transactional
    public RoleDTO create(RoleDTO roleDTO) {
        Role roleEntity = convertToEntity(roleDTO);
        boolean existModule = !roleRepository.findByModule(roleEntity.getModule()).isEmpty();
        if (existModule) {
            throw new IllegalArgumentException("El modulo ya existe");
        }
        roleRepository.save(roleEntity);
        return convertToDto(roleEntity);
    }

    @Override
    @Transactional
    public RoleDTO update(Long id, RoleDTO role) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role no encontrado"));
        if (!existingRole.getModule().equals(Role.Module.valueOf(role.getName()))) {
            existingRole.setModule(Role.Module.valueOf(role.getName()));
            existingRole.setPermission(Role.Permission.valueOf(role.getPermissions().iterator().next()));
            roleRepository.save(existingRole);
            return convertToDto(existingRole);
        } else {
            throw new IllegalArgumentException("El modulo ya existe");
        }
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
    public RoleDTO findById(Long id) {
        if (roleRepository.existsById(id)) {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Role no encontrado"));
            return convertToDto(role);
        } else {
            throw new IllegalArgumentException("El role con el id: " + id + " no existe");
        }
    }

    @Override
    public Page<RoleDTO> listRoles(Pageable pageable) {
        Page<Role> roles = roleRepository.findAll(pageable);
        return roles.map(this::convertToDto);
    }

}
