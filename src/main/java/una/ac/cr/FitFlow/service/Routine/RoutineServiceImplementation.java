package una.ac.cr.FitFlow.service.Routine;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import una.ac.cr.FitFlow.dto.Routine.RoutineInputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForRoutine;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.RoutineActivity;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.RoutineActivityRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineServiceImplementation implements RoutineService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final RoutineActivityRepository routineActivityRepository;
    private final MapperForRoutine mapper;

    private RoutineOutputDTO toDto(Routine r) {
        return mapper.toDto(r);
    }

    @Override
    @Transactional
    public RoutineOutputDTO create(RoutineInputDTO dto) {

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("userId es obligatorio.");
        }
        if (dto.getDaysOfWeek() == null) {
            throw new IllegalArgumentException("daysOfWeek es obligatorio.");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));

        Routine newRoutine = routineRepository.save(mapper.toEntity(dto, user));

        if (dto.getActivityIds() != null && !dto.getActivityIds().isEmpty()) {
            List<RoutineActivity> acts = routineActivityRepository.findAllById(dto.getActivityIds());

            if (acts.size() != dto.getActivityIds().size()) {
                throw new IllegalArgumentException("Uno o más activityIds no existen.");
            }
            for (RoutineActivity a : acts) {
                a.setRoutine(newRoutine);
            }
            newRoutine.getActivities().addAll(acts);
        }

        return toDto(newRoutine);
    }

    @Override
    @Transactional
    public RoutineOutputDTO update(Long id, RoutineInputDTO dto) {
        Routine current = routineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Routine no encontrada: " + id));

        User userIfChanged = null;
        if (dto.getUserId() != null &&
                (current.getUser() == null || !dto.getUserId().equals(current.getUser().getId()))) {
            userIfChanged = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));
        }

        mapper.copyToEntity(dto, current, userIfChanged);

        if (dto.getActivityIds() != null) {
            if (dto.getActivityIds().isEmpty()) {
  
                current.getActivities().clear();
            } else {
                List<RoutineActivity> acts = routineActivityRepository.findAllById(dto.getActivityIds());

                if (acts.size() != dto.getActivityIds().size()) {
                    throw new IllegalArgumentException("Uno o más activityIds no existen.");
                }

                acts.forEach(a -> a.setRoutine(current));
                current.getActivities().clear();
                current.getActivities().addAll(acts);
            }
        }

        Routine saved = routineRepository.save(current);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Routine r = routineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Routine no encontrada: " + id));
        routineRepository.delete(r);
    }

    @Override
    public RoutineOutputDTO findById(Long id) {
        return routineRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Routine no encontrada: " + id));
    }

    @Override
    public Page<RoutineOutputDTO> list(String q, Pageable pageable) {
        Page<Routine> page = (q == null || q.isBlank())
                ? routineRepository.findAll(pageable)
                : routineRepository.findByTitleContainingIgnoreCase(q.trim(), pageable);
        return page.map(this::toDto);
    }

    @Override
    public Page<RoutineOutputDTO> listByUserId(Long userId, Pageable pageable) {
        return routineRepository.findByUserId(userId, pageable).map(this::toDto);
    }
}
