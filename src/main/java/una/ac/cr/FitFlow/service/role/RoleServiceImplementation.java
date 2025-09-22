package una.ac.cr.FitFlow.service.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.Role.RoleInputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForRole;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.repository.RoleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImplementation implements RoleService {

    private final RoleRepository roleRepository;
    private final MapperForRole mapper;

    private String deriveName(Role.Module module, Role.Permission permission) {
        return module.name() + "_" + permission.name();
    }

    private RoleOutputDTO toDto(Role role) {
        return RoleOutputDTO.builder()
                .id(role.getId())
                .name(deriveName(role.getModule(), role.getPermission()))
                .permissions(role.getPermission())
                .module(role.getModule())
                .build();
    }

    @Override
    @Transactional
    public RoleOutputDTO create(RoleInputDTO dto) {
        if (roleRepository.existsByModuleAndPermission(dto.getModule(), dto.getPermissions())) {
            throw new IllegalArgumentException("Ya existe un role con ese (module, permission).");
        }
        Role saved = roleRepository.save(mapper.toEntity(dto));
        return toDto(saved);
    }

    @Override
    @Transactional
    public RoleOutputDTO update(RoleInputDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("El id es obligatorio para actualizar.");
        }
        Role role = roleRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Role no encontrado: id=" + dto.getId()));

        Role.Module targetModule = dto.getModule() != null ? dto.getModule() : role.getModule();
        Role.Permission targetPerm = dto.getPermissions() != null ? dto.getPermissions() : role.getPermission();

        if (roleRepository.existsByModuleAndPermissionAndIdNot(targetModule, targetPerm, role.getId())) {
            throw new IllegalArgumentException("Otro role ya usa ese (module, permission).");
        }

        role.setModule(targetModule);
        role.setPermission(targetPerm);
        return toDto(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El role con id=" + id + " no existe."));
        roleRepository.delete(role);
    }

    @Override
    public RoleOutputDTO findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role no encontrado: id=" + id));
        return toDto(role);
    }

    @Override
    public Page<RoleOutputDTO> listRoles(Pageable pageable) {
        return roleRepository.findAll(pageable).map(this::toDto);
    }
}
