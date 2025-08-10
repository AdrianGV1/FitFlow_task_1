package una.ac.cr.FitFlow.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import una.ac.cr.FitFlow.model.ProgressLog;

public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {
    List<ProgressLog> findByUserIdAndLogDate(Long userId, LocalDate date);
}
