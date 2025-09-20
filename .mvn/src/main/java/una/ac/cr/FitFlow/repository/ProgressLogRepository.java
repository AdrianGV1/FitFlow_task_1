package una.ac.cr.FitFlow.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import una.ac.cr.FitFlow.model.ProgressLog;

public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {

    List<ProgressLog> findByUser_IdAndLogDate(Long userId, LocalDate date);

    Page<ProgressLog> findByUser_Id(Long userId, Pageable pageable);
}
