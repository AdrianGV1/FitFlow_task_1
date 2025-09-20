package una.ac.cr.FitFlow.service.CompletedActivity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityInputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;

public interface CompletedActivityService {
    CompletedActivityOutputDTO createCompletedActivity(CompletedActivityInputDTO input);
    CompletedActivityOutputDTO updateCompletedActivity(Long id, CompletedActivityInputDTO input);
    void deleteCompletedActivity(Long id);
    CompletedActivityOutputDTO findCompletedActivityById(Long id);
    Page<CompletedActivityOutputDTO> listCompletedActivities(String q, Pageable pageable);
    Page<CompletedActivityOutputDTO> findCompletedActivitiesByUserId(Long userId, Pageable pageable);
    Page<CompletedActivityOutputDTO> findByProgressLogId(Long progressLogId, Pageable pageable);
}
