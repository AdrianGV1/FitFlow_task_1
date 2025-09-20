package una.ac.cr.FitFlow.service.CompletedActivity;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityInputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForCompletedActivity;
import una.ac.cr.FitFlow.model.CompletedActivity;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.ProgressLog;
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
    private final MapperForCompletedActivity mapper;

    private CompletedActivityOutputDTO toDto(CompletedActivity c) {
        return mapper.toDto(c);
    }

    @Override
    @Transactional
    public CompletedActivityOutputDTO createCompletedActivity(CompletedActivityInputDTO input) {
      
        if (input.getCompletedAt() == null)
            throw new IllegalArgumentException("completedAt es obligatorio.");
        if (input.getHabitId() == null)
            throw new IllegalArgumentException("habitId es obligatorio.");
        if (input.getProgressLogId() == null)
            throw new IllegalArgumentException("progressLogId es obligatorio.");

        Habit habit = habitRepository.findById(input.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Habit no encontrado: " + input.getHabitId()));

        ProgressLog pl = progressLogRepository.findById(input.getProgressLogId())
                .orElseThrow(() -> new IllegalArgumentException("ProgressLog no encontrado: " + input.getProgressLogId()));

        CompletedActivity entity = CompletedActivity.builder().build();
        mapper.copyBasics(input, entity);
        entity.setHabit(habit);
        entity.setProgressLog(pl);

        CompletedActivity saved = completedActivityRepository.save(entity);
        return toDto(saved);
    }

    @Override
    @Transactional
    public CompletedActivityOutputDTO updateCompletedActivity(Long id, CompletedActivityInputDTO input) {
        CompletedActivity entity = completedActivityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CompletedActivity no encontrado: " + id));

        mapper.copyBasics(input, entity);

        if (input.getHabitId() != null) {
            Habit habit = habitRepository.findById(input.getHabitId())
                    .orElseThrow(() -> new IllegalArgumentException("Habit no encontrado: " + input.getHabitId()));
            entity.setHabit(habit);
        }
        if (input.getProgressLogId() != null) {
            ProgressLog pl = progressLogRepository.findById(input.getProgressLogId())
                    .orElseThrow(() -> new IllegalArgumentException("ProgressLog no encontrado: " + input.getProgressLogId()));
            entity.setProgressLog(pl);
        }

        CompletedActivity saved = completedActivityRepository.save(entity);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteCompletedActivity(Long id) {
        CompletedActivity entity = completedActivityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CompletedActivity no encontrado: " + id));
        completedActivityRepository.delete(entity);
    }

    @Override
    public CompletedActivityOutputDTO findCompletedActivityById(Long id) {
        CompletedActivity entity = completedActivityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CompletedActivity no encontrado: " + id));
        return toDto(entity);
    }

    @Override
    public Page<CompletedActivityOutputDTO> listCompletedActivities(String q, Pageable pageable) {
        if (q == null || q.trim().isEmpty()) {
            return completedActivityRepository.findAll(pageable).map(this::toDto);
        }
        return completedActivityRepository.findByNotesContainingIgnoreCase(q.trim(), pageable)
                .map(this::toDto);
    }

    @Override
    public Page<CompletedActivityOutputDTO> findCompletedActivitiesByUserId(Long userId, Pageable pageable) {
        return completedActivityRepository.findByProgressLog_User_Id(userId, pageable)
                .map(this::toDto);
    }

    @Override
    public Page<CompletedActivityOutputDTO> findByProgressLogId(Long progressLogId, Pageable pageable) {
        return completedActivityRepository.findByProgressLog_Id(progressLogId, pageable)
                .map(this::toDto);
    }
}
