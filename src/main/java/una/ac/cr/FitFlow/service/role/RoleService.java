package una.ac.cr.FitFlow.service.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.RoleDTO;

public interface RoleService {
    RoleDTO create(RoleDTO role);

    RoleDTO update(Long id, RoleDTO role);

    void delete(Long id);

    RoleDTO findById(Long id);

    Page<RoleDTO> listRoles(Pageable pageable);
}
