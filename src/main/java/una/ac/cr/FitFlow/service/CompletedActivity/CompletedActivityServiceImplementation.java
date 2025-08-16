package una.ac.cr.FitFlow.service.CompletedActivity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.CompletedActivityDTO;
import una.ac.cr.FitFlow.model.CompletedActivity;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.CompletedActivityRepository;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.ProgressLogRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompletedActivityServiceImplementation implements CompletedActivityService {
    private final CompletedActivityRepository completedActivityRepository;
    private final HabitRepository habitRepository;
    private final ProgressLogRepository progressLogRepository;

    private CompletedActivityDTO convertToDto(CompletedActivity completedActivity) {
        return CompletedActivityDTO.builder()
                .id(completedActivity.getId())
                .completedAt(completedActivity.getCompletedAt())
                .notes(completedActivity.getNotes())
                .progressLogId(
                        completedActivity.getProgressLog() != null ? completedActivity.getProgressLog().getId() : null)
                .habitId(completedActivity.getHabit() != null ? completedActivity.getHabit().getId() : null)
                .build();
    }

    private CompletedActivity convertToEntity(CompletedActivityDTO dto) {
        CompletedActivity entity = CompletedActivity.builder()
                .id(dto.getId())
                .completedAt(dto.getCompletedAt())
                .notes(dto.getNotes())
                .build();

        if (dto.getHabitId() != null) {
            Habit habit = habitRepository.findById(dto.getHabitId())
                    .orElseThrow(() -> new IllegalArgumentException("Habit no encontrado: " + dto.getHabitId()));
            entity.setHabit(habit);
        }

        if (dto.getProgressLogId() != null) {
            ProgressLog progressLog = progressLogRepository.findById(dto.getProgressLogId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("ProgressLog no encontrado: " + dto.getProgressLogId()));
            entity.setProgressLog(progressLog);
        }

        return entity;
    }

    @Override
    @Transactional
    public CompletedActivityDTO createCompletedActivity(CompletedActivityDTO completedActivityDTO) {
        CompletedActivity completedActivity = convertToEntity(completedActivityDTO);
        completedActivity = completedActivityRepository.save(completedActivity);
        return convertToDto(completedActivity);
    }

    @Override
    @Transactional
    public CompletedActivityDTO updateCompletedActivity(Long id, CompletedActivityDTO completedActivityDTO) {
        CompletedActivity entity = completedActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Completed Activity not found"));
        entity.setCompletedAt(completedActivityDTO.getCompletedAt());
        entity.setNotes(completedActivityDTO.getNotes());
        if (completedActivityDTO.getHabitId() != null) {
            Habit habit = habitRepository.findById(completedActivityDTO.getHabitId())
                    .orElseThrow(() -> new IllegalArgumentException("Habit no encontrado: " +
                            completedActivityDTO.getHabitId()));
            entity.setHabit(habit);
        }
        if (completedActivityDTO.getProgressLogId() != null) {
            ProgressLog progressLog = progressLogRepository.findById(completedActivityDTO.getProgressLogId())
                    .orElseThrow(() -> new IllegalArgumentException("ProgressLog no encontrado: " +
                            completedActivityDTO.getProgressLogId()));
            entity.setProgressLog(progressLog);
        }
        entity = completedActivityRepository.save(entity);
        return convertToDto(entity);
    }

    @Override
    @Transactional
    public void deleteCompletedActivity(Long id) {
        CompletedActivity completedActivity = completedActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Completed Activity not found"));
        completedActivityRepository.delete(completedActivity);
    }

    @Override
    public CompletedActivityDTO findCompletedActivityById(Long id) {
        CompletedActivity completedActivity = completedActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Completed Activity not found"));
        return convertToDto(completedActivity);
    }

    @Override
    public Page<CompletedActivityDTO> listCompletedActivities(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return completedActivityRepository.findAll(pageable).map(this::convertToDto);
        }
        return completedActivityRepository.findByNotesContainingIgnoreCase(q, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<CompletedActivityDTO> findCompletedActivitiesByUserId(Long userId, Pageable pageable) {
        return completedActivityRepository.findByProgressLogUserId(userId, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<CompletedActivityDTO> findByProgressLogId(Long progressLogId, Pageable pageable) {
        return completedActivityRepository.findByProgressLogId(progressLogId, pageable)
                .map(this::convertToDto);
    }

}
