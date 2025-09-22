// ProgressLogService.java
package una.ac.cr.FitFlow.service.ProgressLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;

import java.time.OffsetDateTime;
import java.util.List;

public interface ProgressLogService {
  ProgressLogOutputDTO create(ProgressLogInputDTO in);
  ProgressLogOutputDTO update(Long id, ProgressLogInputDTO in);
  void delete(Long id);
  ProgressLogOutputDTO findById(Long id);
  Page<ProgressLogOutputDTO> list(Pageable pageable);
  Page<ProgressLogOutputDTO> listByUser(Long userId, Pageable pageable);
  List<ProgressLogOutputDTO> listByUserOnDate(Long userId, OffsetDateTime dateAtUserZone);
}
