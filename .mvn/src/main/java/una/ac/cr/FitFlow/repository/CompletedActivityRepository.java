package una.ac.cr.FitFlow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import una.ac.cr.FitFlow.model.CompletedActivity;

public interface CompletedActivityRepository extends JpaRepository<CompletedActivity, Long> {

    Page<CompletedActivity> findByNotesContainingIgnoreCase(String notes, Pageable pageable);

    Page<CompletedActivity> findByProgressLog_User_Id(Long userId, Pageable pageable);

    Page<CompletedActivity> findByProgressLog_Id(Long progressLogId, Pageable pageable);
}
