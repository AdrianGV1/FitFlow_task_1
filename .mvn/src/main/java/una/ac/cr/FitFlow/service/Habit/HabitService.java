package una.ac.cr.FitFlow.service.Habit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.HabitDTO;

public interface HabitService {
    HabitDTO createHabit(HabitDTO habitDTO);
    HabitDTO updateHabit(Long id, HabitDTO habitDTO);
    void deleteHabit(Long id);
    HabitDTO findHabitById(Long id);
    Page<HabitDTO> listHabits(String q, Pageable pageable);
}
