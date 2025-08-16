package una.ac.cr.FitFlow.service.CompletedActivity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.CompletedActivityDTO;

public interface CompletedActivityService {
    CompletedActivityDTO createCompletedActivity(CompletedActivityDTO completedActivityDTO);

    CompletedActivityDTO updateCompletedActivity(Long id, CompletedActivityDTO completedActivityDTO);

    void deleteCompletedActivity(Long id);

    CompletedActivityDTO findCompletedActivityById(Long id);

    Page<CompletedActivityDTO> listCompletedActivities(String q, Pageable pageable);

    Page<CompletedActivityDTO> findCompletedActivitiesByUserId(Long userId, Pageable pageable);

    Page<CompletedActivityDTO> findByProgressLogId(Long progressLogId, Pageable pageable);
}