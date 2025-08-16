package una.ac.cr.FitFlow.service.Habit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.HabitDTO;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.repository.HabitRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitServiceImplementation implements HabitService {
    private final HabitRepository habitRepository;

    private HabitDTO convertToDto(Habit habit) {
        return HabitDTO.builder()
                .id(habit.getId())
                .name(habit.getName())
                .description(habit.getDescription())
                .category(habit.getCategory().toString())
                .build();
    }

    private Habit convertToEntity(HabitDTO habitDTO) {
        return Habit.builder()
                .name(habitDTO.getName())
                .description(habitDTO.getDescription())
                .category(parseCategory(habitDTO.getCategory()))
                .build();
    }

    private Habit.Category parseCategory(String raw) {
        if (raw == null)
            throw new IllegalArgumentException("La categoría es obligatoria");
        try {
            return Habit.Category.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Categoría inválida: " + raw + ". Use PHYSICAL, MENTAL, SLEEP o DIET.");
        }
    }

    @Override
    @Transactional
    public HabitDTO createHabit(HabitDTO habitDTO) {
        if (habitRepository.existsByName(habitDTO.getName())) {
            throw new IllegalArgumentException("El nombre del habito ya esta en uso");
        } else if (habitDTO.getName() == null || habitDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("El nombre del hábito es obligatorio");
        }
        Habit newHabit = convertToEntity(habitDTO);
        habitRepository.save(newHabit);
        return convertToDto(newHabit);
    }

    @Override
    @Transactional
    public HabitDTO updateHabit(Long id, HabitDTO habitDTO) {
        Habit existingHabit = habitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado"));

        if (habitDTO.getName() == null || habitDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("El nombre del hábito es obligatorio");
        }
        else if (!existingHabit.getName().equals(habitDTO.getName())) {
            existingHabit.setName(habitDTO.getName());
            existingHabit.setDescription(habitDTO.getDescription());
            existingHabit.setCategory(Habit.Category.valueOf(habitDTO.getCategory()));
            habitRepository.save(existingHabit);
            return convertToDto(existingHabit);
        } else {
            throw new IllegalArgumentException("El nombre del hábito ya está en uso por otro hábito");
        }
    }

    @Override
    @Transactional
    public void deleteHabit(Long id) {
        if (habitRepository.existsById(id)) {
            habitRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Hábito no encontrado");
        }
    }

    @Override
    public HabitDTO findHabitById(Long id) {
        if (habitRepository.existsById(id)) {
            Habit habit = habitRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado"));
            return convertToDto(habit);
        } else {
            throw new IllegalArgumentException("Hábito no encontrado");
        }
    }

    @Override
    public Page<HabitDTO> listHabits(String q, Pageable pageable) {
        if (q == null || q.isEmpty()) {
            return habitRepository.findAll(pageable).map(this::convertToDto);
        } else {
            return habitRepository.findByNameContainingIgnoreCase(q, pageable).map(this::convertToDto);
        }
    }
}
