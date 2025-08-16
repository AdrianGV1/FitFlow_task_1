package una.ac.cr.FitFlow.service.ProgressLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.ProgressLogDTO;

public interface ProgressLogService {
    ProgressLogDTO create(ProgressLogDTO dto);

    ProgressLogDTO update(Long id, ProgressLogDTO dto);

    void delete(Long id);

    ProgressLogDTO findById(Long id);

    Page<ProgressLogDTO> list(Pageable pageable);

    Page<ProgressLogDTO> listByUserId(Long userId, Pageable pageable);

    List<ProgressLogDTO> findByUserIdAndDate(Long userId, LocalDate date);
}
