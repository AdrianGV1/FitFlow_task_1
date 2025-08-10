package una.ac.cr.FitFlow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import una.ac.cr.FitFlow.model.RoutineActivity;

public interface RoutineActivityRepository extends JpaRepository<RoutineActivity, Long> {
    boolean existsByHabitId(Long habitId);
    boolean existsByRoutineId(Long routineId);
}
