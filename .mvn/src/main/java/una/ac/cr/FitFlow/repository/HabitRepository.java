package una.ac.cr.FitFlow.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import una.ac.cr.FitFlow.model.Habit;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    Page<Habit> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Habit> findByCategory(Habit.Category category, Pageable pageable);
    boolean existsByName(String name);
    void deleteByName(String name);
}
