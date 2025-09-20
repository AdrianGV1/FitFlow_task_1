package una.ac.cr.FitFlow.service.RoutineActivity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityInputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForRoutineActivity;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.RoutineActivity;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.RoutineActivityRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineActivityServiceImplementation implements RoutineActivityService {

    private final RoutineActivityRepository routineActivityRepository;
    private final RoutineRepository routineRepository;
    private final HabitRepository habitRepository;
    private final MapperForRoutineActivity mapper;

    private RoutineActivityOutputDTO toDto(RoutineActivity ra) { return mapper.toDto(ra); }

    @Override
    @Transactional
    public RoutineActivityOutputDTO create(RoutineActivityInputDTO dto) {
        if (dto.getRoutineId() == null) throw new IllegalArgumentException("routineId es obligatorio.");
        if (dto.getHabitId() == null)   throw new IllegalArgumentException("habitId es obligatorio.");
        if (dto.getDuration() == null || dto.getDuration() < 1)
            throw new IllegalArgumentException("duration debe ser >= 1.");

        if (routineActivityRepository.existsByRoutine_IdAndHabit_Id(dto.getRoutineId(), dto.getHabitId())) {
            throw new IllegalArgumentException("Ya existe esa actividad para (routineId, habitId).");
        }

        Routine routine = routineRepository.findById(dto.getRoutineId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));
        Habit habit = habitRepository.findById(dto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));

        RoutineActivity saved = routineActivityRepository.save(mapper.toEntity(dto, routine, habit));
        return toDto(saved);
    }

    @Override
    @Transactional
    public RoutineActivityOutputDTO update(Long id, RoutineActivityInputDTO dto) {
        RoutineActivity current = routineActivityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RoutineActivity no encontrada: " + id));

        Long newRoutineId = (dto.getRoutineId() != null) ? dto.getRoutineId()
                : (current.getRoutine() != null ? current.getRoutine().getId() : null);
        Long newHabitId = (dto.getHabitId() != null) ? dto.getHabitId()
                : (current.getHabit() != null ? current.getHabit().getId() : null);

        boolean keyChanged =
                (current.getRoutine() == null || !current.getRoutine().getId().equals(newRoutineId)) ||
                (current.getHabit() == null   || !current.getHabit().getId().equals(newHabitId));

        if (keyChanged && routineActivityRepository.existsByRoutine_IdAndHabit_Id(newRoutineId, newHabitId)) {
            throw new IllegalArgumentException("Ya existe esa actividad para (routineId, habitId).");
        }

        Routine routineIfChanged = null;
        if (dto.getRoutineId() != null &&
                (current.getRoutine() == null || !dto.getRoutineId().equals(current.getRoutine().getId()))) {
            routineIfChanged = routineRepository.findById(dto.getRoutineId())
                    .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));
        }

        Habit habitIfChanged = null;
        if (dto.getHabitId() != null &&
                (current.getHabit() == null || !dto.getHabitId().equals(current.getHabit().getId()))) {
            habitIfChanged = habitRepository.findById(dto.getHabitId())
                    .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));
        }

        if (dto.getDuration() != null && dto.getDuration() < 1) {
            throw new IllegalArgumentException("duration debe ser >= 1.");
        }

        mapper.copyToEntity(dto, current, routineIfChanged, habitIfChanged);

        RoutineActivity saved = routineActivityRepository.save(current);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!routineActivityRepository.existsById(id)) {
            throw new IllegalArgumentException("RoutineActivity no encontrada: " + id);
        }
        routineActivityRepository.deleteById(id);
    }

    @Override
    public RoutineActivityOutputDTO findById(Long id) {
        return routineActivityRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("RoutineActivity no encontrada: " + id));
    }

    @Override
    public Page<RoutineActivityOutputDTO> list(Pageable pageable) {
        return routineActivityRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<RoutineActivityOutputDTO> listByRoutineId(Long routineId, Pageable pageable) {
        return routineActivityRepository.findByRoutine_Id(routineId, pageable).map(this::toDto);
    }

    @Override
    public Page<RoutineActivityOutputDTO> listByHabitId(Long habitId, Pageable pageable) {
        return routineActivityRepository.findByHabit_Id(habitId, pageable).map(this::toDto);
    }
}
