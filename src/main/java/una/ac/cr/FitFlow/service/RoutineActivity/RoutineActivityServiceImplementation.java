package una.ac.cr.FitFlow.service.RoutineActivity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.RoutineActivityDTO;
import una.ac.cr.FitFlow.model.RoutineActivity;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.repository.RoutineActivityRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;
import una.ac.cr.FitFlow.repository.HabitRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class RoutineActivityServiceImplementation implements RoutineActivityService {
    private final RoutineActivityRepository routineActivityRepository;
    private final RoutineRepository routineRepository;
    private final HabitRepository habitRepository;

    private RoutineActivityDTO toDto(RoutineActivity ra) {
        return RoutineActivityDTO.builder()
                .id(ra.getId())
                .routineId(ra.getRoutine() != null ? ra.getRoutine().getId() : null)
                .habitId(ra.getHabit() != null ? ra.getHabit().getId() : null)
                .duration(ra.getDuration())
                .notes(ra.getNotes())
                .build();
    }

    private RoutineActivity toEntityForCreate(RoutineActivityDTO dto) {
        Routine routine = routineRepository.findById(dto.getRoutineId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));
        Habit habit = habitRepository.findById(dto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));

        return RoutineActivity.builder()
                .routine(routine)
                .habit(habit)
                .duration(dto.getDuration())
                .notes(dto.getNotes())
                .build();
    }

    private void applyForUpdate(RoutineActivity target, RoutineActivityDTO dto) {
        if (dto.getRoutineId() != null &&
                (target.getRoutine() == null || !dto.getRoutineId().equals(target.getRoutine().getId()))) {
            Routine routine = routineRepository.findById(dto.getRoutineId())
                    .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));
            target.setRoutine(routine);
        }
        if (dto.getHabitId() != null &&
                (target.getHabit() == null || !dto.getHabitId().equals(target.getHabit().getId()))) {
            Habit habit = habitRepository.findById(dto.getHabitId())
                    .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));
            target.setHabit(habit);
        }
        if (dto.getDuration() != null)
            target.setDuration(dto.getDuration());
        if (dto.getNotes() != null)
            target.setNotes(dto.getNotes());
    }

    @Override
    public RoutineActivityDTO create(RoutineActivityDTO dto) {
        if (routineActivityRepository.existsByRoutineIdAndHabitId(dto.getRoutineId(), dto.getHabitId())) {
            throw new IllegalArgumentException("Ya existe esa actividad en la rutina (routineId, habitId)");
        }
        RoutineActivity saved = routineActivityRepository.save(toEntityForCreate(dto));
        return toDto(saved);
    }

    @Override
    @Transactional
    public RoutineActivityDTO update(Long id, RoutineActivityDTO dto) {
        RoutineActivity current = routineActivityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RoutineActivity no encontrada: " + id));

        Long newRoutineId = dto.getRoutineId() != null ? dto.getRoutineId()
                : (current.getRoutine() != null ? current.getRoutine().getId() : null);
        Long newHabitId = dto.getHabitId() != null ? dto.getHabitId()
                : (current.getHabit() != null ? current.getHabit().getId() : null);
        boolean keyChanged = (current.getRoutine() == null || !current.getRoutine().getId().equals(newRoutineId)) ||
                (current.getHabit() == null || !current.getHabit().getId().equals(newHabitId));
        if (keyChanged && routineActivityRepository.existsByRoutineIdAndHabitId(newRoutineId, newHabitId)) {
            throw new IllegalArgumentException("Ya existe esa actividad en la rutina (routineId, habitId)");
        }
        applyForUpdate(current, dto);
        return toDto(routineActivityRepository.save(current));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!routineActivityRepository.existsById(id))
            throw new IllegalArgumentException("RoutineActivity no encontrada: " + id);
        routineActivityRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineActivityDTO findById(Long id) {
        return routineActivityRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("RoutineActivity no encontrada: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineActivityDTO> list(Pageable pageable) {
        return routineActivityRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineActivityDTO> listByRoutineId(Long routineId, Pageable pageable) {
        return routineActivityRepository.findByRoutineId(routineId, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineActivityDTO> listByHabitId(Long habitId, Pageable pageable) {
        return routineActivityRepository.findByHabitId(habitId, pageable).map(this::toDto);
    }
}
