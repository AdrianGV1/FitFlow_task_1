package una.ac.cr.FitFlow.service.ProgressLog;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;

public interface ProgressLogService {
    ProgressLogOutputDTO create(ProgressLogInputDTO dto);
    ProgressLogOutputDTO update(Long id, ProgressLogInputDTO dto);
    void delete(Long id);
    ProgressLogOutputDTO findById(Long id);
    Page<ProgressLogOutputDTO> list(Pageable pageable);
    Page<ProgressLogOutputDTO> listByUserId(Long userId, Pageable pageable);
    List<ProgressLogOutputDTO> findByUserIdAndDate(Long userId, LocalDate date);
}
