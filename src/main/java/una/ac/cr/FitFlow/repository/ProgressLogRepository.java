package una.ac.cr.FitFlow.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import una.ac.cr.FitFlow.model.ProgressLog;

public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {
    List<ProgressLog> findByUserIdAndLogDate(Long userId, LocalDate date);
    Page<ProgressLog> findByUserId(Long userId, Pageable p);
}
