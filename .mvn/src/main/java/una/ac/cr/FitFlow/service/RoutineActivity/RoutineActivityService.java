package una.ac.cr.FitFlow.service.RoutineActivity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityInputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;

public interface RoutineActivityService {
    RoutineActivityOutputDTO create(RoutineActivityInputDTO dto);
    RoutineActivityOutputDTO update(Long id, RoutineActivityInputDTO dto);
    void delete(Long id);
    RoutineActivityOutputDTO findById(Long id);
    Page<RoutineActivityOutputDTO> list(Pageable pageable);
    Page<RoutineActivityOutputDTO> listByRoutineId(Long routineId, Pageable pageable);
    Page<RoutineActivityOutputDTO> listByHabitId(Long habitId, Pageable pageable);
}
