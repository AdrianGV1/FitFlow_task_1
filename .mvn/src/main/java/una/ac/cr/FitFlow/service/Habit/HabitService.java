package una.ac.cr.FitFlow.service.Habit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.Habit.HabitInputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;

public interface HabitService {
    HabitOutputDTO createHabit(HabitInputDTO input);
    HabitOutputDTO updateHabit(Long id, HabitInputDTO input);
    void deleteHabit(Long id);
    HabitOutputDTO findHabitById(Long id);
    Page<HabitOutputDTO> listHabits(String q, Pageable pageable);
}
