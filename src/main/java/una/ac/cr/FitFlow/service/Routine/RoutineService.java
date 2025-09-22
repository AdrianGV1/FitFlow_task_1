package una.ac.cr.FitFlow.service.Routine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.Routine.RoutineInputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;

public interface RoutineService {
    RoutineOutputDTO create(RoutineInputDTO dto);
    RoutineOutputDTO update(Long id, RoutineInputDTO dto);
    void delete(Long id);
    RoutineOutputDTO findById(Long id);
    Page<RoutineOutputDTO> list(String q, Pageable pageable);
    Page<RoutineOutputDTO> listByUserId(Long userId, Pageable pageable);
}
