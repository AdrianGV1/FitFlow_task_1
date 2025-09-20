package una.ac.cr.FitFlow.service.ProgressLog;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForProgressLog;
import una.ac.cr.FitFlow.model.CompletedActivity;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.CompletedActivityRepository;
import una.ac.cr.FitFlow.repository.ProgressLogRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressLogServiceImplementation implements ProgressLogService {

    private final ProgressLogRepository progressLogRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final CompletedActivityRepository completedActivityRepository;
    private final MapperForProgressLog mapper;

    private ProgressLogOutputDTO toDto(ProgressLog pl) { return mapper.toDto(pl); }

    @Override
    @Transactional
    public ProgressLogOutputDTO create(ProgressLogInputDTO dto) {
        if (dto.getUserId() == null)    throw new IllegalArgumentException("userId es obligatorio.");
        if (dto.getRoutineId() == null) throw new IllegalArgumentException("routineId es obligatorio.");
        if (dto.getDate() == null)      throw new IllegalArgumentException("date es obligatorio.");

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));
        Routine routine = routineRepository.findById(dto.getRoutineId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));

        ProgressLog pl = mapper.toEntity(dto, user, routine);

        if (dto.getCompletedActivityIds() != null) {
            List<CompletedActivity> acts = dto.getCompletedActivityIds().isEmpty()
                    ? Collections.<CompletedActivity>emptyList()
                    : completedActivityRepository.findAllById(dto.getCompletedActivityIds());

            if (!dto.getCompletedActivityIds().isEmpty() && acts.size() != dto.getCompletedActivityIds().size()) {
                throw new IllegalArgumentException("Uno o más completedActivityIds no existen.");
            }

            acts.forEach(a -> a.setProgressLog(pl));
            pl.setCompletedActivities(acts);
        }

        ProgressLog saved = progressLogRepository.save(pl);
        return toDto(saved);
    }

    @Override
    @Transactional
    public ProgressLogOutputDTO update(Long id, ProgressLogInputDTO dto) {
        ProgressLog current = progressLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProgressLog no encontrado: " + id));

        User userIfChanged = null;
        if (dto.getUserId() != null &&
                (current.getUser() == null || !dto.getUserId().equals(current.getUser().getId()))) {
            userIfChanged = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));
        }

        Routine routineIfChanged = null;
        if (dto.getRoutineId() != null &&
                (current.getRoutine() == null || !dto.getRoutineId().equals(current.getRoutine().getId()))) {
            routineIfChanged = routineRepository.findById(dto.getRoutineId())
                    .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));
        }

        mapper.copyToEntity(dto, current, userIfChanged, routineIfChanged);

        if (dto.getCompletedActivityIds() != null) {
            List<CompletedActivity> acts = dto.getCompletedActivityIds().isEmpty()
                    ? Collections.<CompletedActivity>emptyList()
                    : completedActivityRepository.findAllById(dto.getCompletedActivityIds());
            acts.forEach(a -> a.setProgressLog(current));
            current.getCompletedActivities().clear();
            current.getCompletedActivities().addAll(acts);
        }

        ProgressLog saved = progressLogRepository.save(current);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!progressLogRepository.existsById(id)) {
            throw new IllegalArgumentException("ProgressLog no encontrado: " + id);
        }
        progressLogRepository.deleteById(id);
    }

    @Override
    public ProgressLogOutputDTO findById(Long id) {
        return progressLogRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("ProgressLog no encontrado: " + id));
    }

    @Override
    public Page<ProgressLogOutputDTO> list(Pageable pageable) {
        return progressLogRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<ProgressLogOutputDTO> listByUserId(Long userId, Pageable pageable) {
        return progressLogRepository.findByUser_Id(userId, pageable).map(this::toDto);
    }

    @Override
    public List<ProgressLogOutputDTO> findByUserIdAndDate(Long userId, java.time.LocalDate date) {
        return progressLogRepository.findByUser_IdAndLogDate(userId, date)
                .stream()
                .map(this::toDto)
                .collect(java.util.stream.Collectors.toList());
    }
}
