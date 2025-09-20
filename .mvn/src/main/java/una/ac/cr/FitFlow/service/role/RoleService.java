package una.ac.cr.FitFlow.service.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.Role.RoleInputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;

public interface RoleService {
    RoleOutputDTO create(RoleInputDTO role);
    RoleOutputDTO update(RoleInputDTO role);
    void delete(Long id);
    RoleOutputDTO findById(Long id);
    Page<RoleOutputDTO> listRoles(Pageable pageable);
}
