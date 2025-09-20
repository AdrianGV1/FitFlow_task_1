package una.ac.cr.FitFlow.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import una.ac.cr.FitFlow.model.RoutineActivity;

public interface RoutineActivityRepository extends JpaRepository<RoutineActivity, Long> {

    boolean existsByRoutine_IdAndHabit_Id(Long routineId, Long habitId);
    Optional<RoutineActivity> findByRoutine_IdAndHabit_Id(Long routineId, Long habitId);

    Page<RoutineActivity> findByRoutine_Id(Long routineId, Pageable pageable);
    Page<RoutineActivity> findByHabit_Id(Long habitId, Pageable pageable);
}
