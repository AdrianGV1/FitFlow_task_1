package una.ac.cr.FitFlow.service.Habit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import una.ac.cr.FitFlow.dto.Habit.HabitInputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForHabit;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.repository.HabitRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitServiceImplementation implements HabitService {

    private final HabitRepository habitRepository;
    private final MapperForHabit mapper;

    @Override
    @Transactional
    public HabitOutputDTO createHabit(HabitInputDTO in) {
        
        if (in.getName() == null || in.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del hábito es obligatorio.");
        }
        if (in.getCategory() == null || in.getCategory().isBlank()) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        if (habitRepository.existsByName(in.getName())) {
            throw new IllegalArgumentException("El nombre del hábito ya está en uso.");
        }

        Habit newHabit = mapper.toEntity(in);  
        Habit saved = habitRepository.save(newHabit);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public HabitOutputDTO updateHabit(Long id, HabitInputDTO in) {
        Habit existing = habitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado."));

        
        if (in.getName() != null) {
            if (in.getName().isBlank())
                throw new IllegalArgumentException("El nombre del hábito no puede ser vacío.");
            boolean changingName = !in.getName().equals(existing.getName());
            if (changingName && habitRepository.existsByName(in.getName())) {
                throw new IllegalArgumentException("El nombre del hábito ya está en uso por otro hábito.");
            }
        }

        mapper.copyToEntity(in, existing);

        Habit saved = habitRepository.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteHabit(Long id) {
        Habit h = habitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado."));
        habitRepository.delete(h);
    }

    @Override
    public HabitOutputDTO findHabitById(Long id) {
        Habit h = habitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado."));
        return mapper.toDto(h);
    }

    @Override
    public Page<HabitOutputDTO> listHabits(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return habitRepository.findAll(pageable).map(mapper::toDto);
        }
        
        try {
            Habit.Category cat = Habit.Category.valueOf(q.trim().toUpperCase());
            return habitRepository.findByCategory(cat, pageable).map(mapper::toDto);
        } catch (IllegalArgumentException ignored) {
            
            return habitRepository.findByNameContainingIgnoreCase(q, pageable).map(mapper::toDto);
        }
    }
}
