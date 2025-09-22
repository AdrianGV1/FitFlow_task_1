// ProgressLogRepository.java
package una.ac.cr.FitFlow.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import una.ac.cr.FitFlow.model.ProgressLog;

public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {

  Page<ProgressLog> findByUser_Id(Long userId, Pageable pageable);
  Page<ProgressLog> findByRoutine_Id(Long routineId, Pageable pageable);
  Page<ProgressLog> findByUser_IdAndRoutine_Id(Long userId, Long routineId, Pageable pageable);

  Optional<ProgressLog> findTopByUser_IdOrderByLogDateDesc(Long userId);

  List<ProgressLog> findByUser_IdAndLogDateBetween(
      Long userId, OffsetDateTime startInclusive, OffsetDateTime endExclusive);
}
