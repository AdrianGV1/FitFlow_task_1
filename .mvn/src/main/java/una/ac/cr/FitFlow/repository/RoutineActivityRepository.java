package una.ac.cr.FitFlow.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import una.ac.cr.FitFlow.model.RoutineActivity;

public interface RoutineActivityRepository extends JpaRepository<RoutineActivity, Long> {
    boolean existsByHabitId(Long habitId);
    boolean existsByRoutineId(Long routineId);

    // ↓ para respetar la uniqueConstraint (routine_id, habit_id)
    boolean existsByRoutineIdAndHabitId(Long routineId, Long habitId);
    Optional<RoutineActivity> findByRoutineIdAndHabitId(Long routineId, Long habitId);

    // ↓ soporte de listados paginados
    Page<RoutineActivity> findByRoutineId(Long routineId, Pageable pageable);
    Page<RoutineActivity> findByHabitId(Long habitId, Pageable pageable);
}
