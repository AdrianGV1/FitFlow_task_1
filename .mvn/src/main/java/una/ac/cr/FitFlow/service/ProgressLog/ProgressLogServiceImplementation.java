package una.ac.cr.FitFlow.service.ProgressLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import una.ac.cr.FitFlow.dto.ProgressLogDTO;
import una.ac.cr.FitFlow.model.CompletedActivity;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.CompletedActivityRepository;
import una.ac.cr.FitFlow.repository.ProgressLogRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressLogServiceImplementation implements ProgressLogService {
    private final ProgressLogRepository progressLogRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final CompletedActivityRepository completedActivityRepository;

    private ProgressLogDTO convertToDto(ProgressLog progressLog) {
        return ProgressLogDTO.builder()
                .id(progressLog.getId())
                .userId(progressLog.getUser() != null ? progressLog.getUser().getId() : null)
                .routineId(progressLog.getRoutine() != null ? progressLog.getRoutine().getId() : null)
                .date(progressLog.getLogDate() != null ? progressLog.getLogDate().atStartOfDay() : null)
                .completedActivityIds(
                        progressLog.getCompletedActivities() == null ? List.of()
                                : progressLog.getCompletedActivities().stream()
                                        .map(CompletedActivity::getId)
                                        .filter(Objects::nonNull)
                                        .toList())
                .build();
    }

    private ProgressLog convertToEntity(ProgressLogDTO progressLogDto) {
        User user = userRepository.findById(progressLogDto.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("Usuario no encontrado: " + progressLogDto.getUserId()));
        Routine routine = routineRepository.findById(progressLogDto.getRoutineId()).orElseThrow(
                () -> new IllegalArgumentException("Rutina no encontrada: " + progressLogDto.getRoutineId()));

        ProgressLog progressLog = ProgressLog.builder()
                .user(user)
                .routine(routine)
                .logDate(progressLogDto.getDate().toLocalDate())
                .build();

        if (progressLogDto.getCompletedActivityIds() != null && !progressLogDto.getCompletedActivityIds().isEmpty()) {
            List<CompletedActivity> acts = completedActivityRepository
                    .findAllById(progressLogDto.getCompletedActivityIds());
            acts.forEach(a -> a.setProgressLog(progressLog));
            progressLog.setCompletedActivities(acts);
        }

        return progressLog;
    }

    private void applyForUpdate(ProgressLog target, ProgressLogDTO dto) {
        if (dto.getUserId() != null
                && (target.getUser() == null || !dto.getUserId().equals(target.getUser().getId()))) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));
            target.setUser(user);
        }
        if (dto.getRoutineId() != null
                && (target.getRoutine() == null || !dto.getRoutineId().equals(target.getRoutine().getId()))) {
            Routine routine = routineRepository.findById(dto.getRoutineId())
                    .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));
            target.setRoutine(routine);
        }
        if (dto.getDate() != null) {
            target.setLogDate(dto.getDate().toLocalDate());
        }
        if (dto.getCompletedActivityIds() != null) {
            List<CompletedActivity> acts = completedActivityRepository.findAllById(dto.getCompletedActivityIds());
            acts.forEach(a -> a.setProgressLog(target));
            target.getCompletedActivities().clear();
            target.getCompletedActivities().addAll(acts);
        }
    }

    @Override
    @Transactional
    public ProgressLogDTO create(ProgressLogDTO dto) {
        if (dto.getDate() == null) {
            throw new IllegalArgumentException("La fecha (date) es requerida");
        }
        ProgressLog saved = progressLogRepository.save(convertToEntity(dto));
        return convertToDto(saved);
    }

    @Override
    @Transactional
    public ProgressLogDTO update(Long id, ProgressLogDTO dto) {
        ProgressLog pl = progressLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProgressLog no encontrado: " + id));
        applyForUpdate(pl, dto);
        ProgressLog saved = progressLogRepository.save(pl);
        return convertToDto(saved);
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
    @Transactional(readOnly = true)
    public ProgressLogDTO findById(Long id) {
        return progressLogRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new IllegalArgumentException("ProgressLog no encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProgressLogDTO> list(Pageable pageable) {
        return progressLogRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProgressLogDTO> listByUserId(Long userId, Pageable pageable) {
        return progressLogRepository
                .findByUserId(userId, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgressLogDTO> findByUserIdAndDate(Long userId, LocalDate date) {
        return progressLogRepository.findByUserIdAndLogDate(userId, date)
                .stream()
                .map(this::convertToDto)
                .toList();
    }
}
