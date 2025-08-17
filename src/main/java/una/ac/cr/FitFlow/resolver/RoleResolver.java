package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.RoleDTO;
import una.ac.cr.FitFlow.service.role.RoleService;

@Controller
@RequiredArgsConstructor
public class RoleResolver {
    private final RoleService roleService;

    @QueryMapping
    public RoleDTO getRoleById(@Argument Long id) {
        return roleService.findById(id);
    }

    @QueryMapping
    public Page<RoleDTO> getAllRoles(@Argument int page, @Argument int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return roleService.listRoles(pageable);
    }

    @MutationMapping
    public RoleDTO createRole(@Argument RoleDTO roleDTO) {
        return roleService.create(roleDTO);
    }

    @MutationMapping
    public RoleDTO updateRole(@Argument Long id, @Argument RoleDTO roleDTO) {
        return roleService.update(id, roleDTO);
    }

    @MutationMapping
    public void deleteRole(@Argument Long id) {
        roleService.delete(id);
    }

}
