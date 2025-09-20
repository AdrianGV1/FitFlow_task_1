package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.Role.RoleInputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.model.Role;

@Component
public class MapperForRole {

    private String deriveName(Role.Module module, Role.Permission permission) {
        return module.name() + "_" + permission.name();
    }

    public RoleOutputDTO toDto(Role role) {
        if (role == null) return null;
        return RoleOutputDTO.builder()
                .id(role.getId())
                .name(deriveName(role.getModule(), role.getPermission()))
                .permissions(role.getPermission())
                .module(role.getModule())
                .build();
    }

    public Role toEntity(RoleInputDTO dto) {
        if (dto == null) return null;
        return Role.builder()
                .id(dto.getId()) 
                .module(dto.getModule())
                .permission(dto.getPermissions())
                .build();
    }

    public void copyToEntity(RoleInputDTO dto, Role target) {
        if (dto.getModule() != null)      target.setModule(dto.getModule());
        if (dto.getPermissions() != null) target.setPermission(dto.getPermissions());
    }
}
