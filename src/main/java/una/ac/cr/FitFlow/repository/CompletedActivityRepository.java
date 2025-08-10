package una.ac.cr.FitFlow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import una.ac.cr.FitFlow.model.CompletedActivity;

public interface CompletedActivityRepository extends JpaRepository<CompletedActivity, Long> {
    List<CompletedActivity> findByProgressLogId(Long progressLogId);
}