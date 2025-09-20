package una.ac.cr.FitFlow.service.RoutineActivity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.RoutineActivityDTO;

public interface RoutineActivityService {
    RoutineActivityDTO create(RoutineActivityDTO dto);

    RoutineActivityDTO update(Long id, RoutineActivityDTO dto);

    void delete(Long id);

    RoutineActivityDTO findById(Long id);

    Page<RoutineActivityDTO> list(Pageable pageable);

    Page<RoutineActivityDTO> listByRoutineId(Long routineId, Pageable pageable);

    Page<RoutineActivityDTO> listByHabitId(Long habitId, Pageable pageable);
}
