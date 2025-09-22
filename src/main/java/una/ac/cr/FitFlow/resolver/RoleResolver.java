package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.Role.RoleInputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.dto.Role.RolePageDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.role.RoleService;

@Controller
@RequiredArgsConstructor
public class RoleResolver {
    private static final Role.Module MODULE = Role.Module.RUTINAS;
    private final RoleService roleService;


    @QueryMapping(name = "roleById")
    public RoleOutputDTO roleById(@Argument("id") Long id) {
        SecurityUtils.requireRead(MODULE);
        return roleService.findById(id);
    }

    @QueryMapping(name = "roles")
    public RolePageDTO roles(@Argument("page") int page, @Argument("size") int size) {
        SecurityUtils.requireRead(MODULE);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<RoleOutputDTO> rolePage = roleService.listRoles(pageable);
        return RolePageDTO.builder()
                .content(rolePage.getContent())
                .totalElements(rolePage.getTotalElements())
                .totalPages(rolePage.getTotalPages())
                .pageNumber(rolePage.getNumber())
                .pageSize(rolePage.getSize())
                .build();
    }

    @MutationMapping(name = "createRole")
    public RoleOutputDTO createRole(@Argument("input") RoleInputDTO roleDTO) {
        SecurityUtils.requireWrite(MODULE);
        return roleService.create(roleDTO);
    }

    @MutationMapping(name = "updateRole")
    public RoleOutputDTO updateRole(@Argument("input") RoleInputDTO roleDTO) {
        SecurityUtils.requireWrite(MODULE);
        return roleService.update(roleDTO);
    }

    @MutationMapping(name = "deleteRole")
    public Boolean deleteRole(@Argument("id") Long id) {
        SecurityUtils.requireWrite(MODULE);
        roleService.delete(id);
        return true;
    }

}

