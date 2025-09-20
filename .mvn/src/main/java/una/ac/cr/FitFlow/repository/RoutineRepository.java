package una.ac.cr.FitFlow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import una.ac.cr.FitFlow.model.Routine;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    Page<Routine> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Routine> findByUserId(Long userId, Pageable pageable);
}
