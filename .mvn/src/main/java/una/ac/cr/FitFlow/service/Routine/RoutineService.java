package una.ac.cr.FitFlow.service.Routine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.RoutineDTO;

public interface RoutineService {
    RoutineDTO create(RoutineDTO dto);

    RoutineDTO update(Long id, RoutineDTO dto);

    void delete(Long id);

    RoutineDTO findById(Long id);

    Page<RoutineDTO> list(String q, Pageable pageable);

    Page<RoutineDTO> listByUserId(Long userId, Pageable pageable);
}
