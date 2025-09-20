package una.ac.cr.FitFlow.service.Routine;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.RoutineDTO;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.RoutineActivity;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.RoutineActivityRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineServiceImplementation implements RoutineService {
    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final RoutineActivityRepository routineActivityRepository;

    private String daysToString(Set<String> days) {
        if (days == null || days.isEmpty())
            return "";
        return days.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    private Set<String> daysToSet(String days) {
        if (days == null || days.isBlank())
            return java.util.Set.of();
        return java.util.Arrays.stream(days.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    private RoutineDTO convertToDto(Routine routine) {
        return RoutineDTO.builder()
                .id(routine.getId())
                .title(routine.getTitle())
                .userId(routine.getUser() != null ? routine.getUser().getId() : null)
                .daysOfWeek(daysToSet(routine.getDaysOfWeek()))
                .activityIds(
                        routine.getActivities() == null ? List.of()
                                : routine.getActivities().stream()
                                        .map(RoutineActivity::getId)
                                        .filter(Objects::nonNull)
                                        .toList())
                .build();
    }

    private Routine convertToEntity(RoutineDTO routineDto) {
        User user = userRepository.findById(routineDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + routineDto.getUserId()));
        return Routine.builder()
                .title(routineDto.getTitle())
                .user(user)
                .daysOfWeek(daysToString(routineDto.getDaysOfWeek()))
                .build();
    }

    private void applyForUpdate(Routine target, RoutineDTO routineDTO) {
        if (routineDTO.getTitle() != null)
            target.setTitle(routineDTO.getTitle());

        if (routineDTO.getUserId() != null &&
                (target.getUser() == null || !routineDTO.getUserId().equals(target.getUser().getId()))) {
            User user = userRepository.findById(routineDTO.getUserId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Usuario no encontrado: " + routineDTO.getUserId()));
            target.setUser(user);
        }

        if (routineDTO.getDaysOfWeek() != null) {
            target.setDaysOfWeek(daysToString(routineDTO.getDaysOfWeek()));
        }
        if (routineDTO.getActivityIds() != null) {
            List<RoutineActivity> acts = routineDTO.getActivityIds().isEmpty()
                    ? List.of()
                    : routineActivityRepository.findAllById(routineDTO.getActivityIds());
            acts.forEach(a -> a.setRoutine(target));
            target.getActivities().clear();
            target.getActivities().addAll(acts);
        }
    }

    @Override
    @Transactional
    public RoutineDTO create(RoutineDTO dto) {
        Routine newRoutine = routineRepository.save(convertToEntity(dto));
        if (dto.getActivityIds() != null && !dto.getActivityIds().isEmpty()) {
            List<RoutineActivity> acts = routineActivityRepository.findAllById(dto.getActivityIds());
            acts.forEach(a -> a.setRoutine(newRoutine));
            newRoutine.getActivities().addAll(acts);
            Routine updatedRoutine = routineRepository.save(newRoutine);
            return convertToDto(updatedRoutine);
        }
        return convertToDto(newRoutine);
    }

    @Override
    @Transactional
    public RoutineDTO update(Long id, RoutineDTO dto) {
        Routine current = routineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Routine no encontrada: " + id));
        applyForUpdate(current, dto);
        return convertToDto(routineRepository.save(current));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!routineRepository.existsById(id))
            throw new IllegalArgumentException("Routine no encontrada: " + id);
        routineRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineDTO findById(Long id) {
        return routineRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new IllegalArgumentException("Routine no encontrada: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineDTO> list(String q, Pageable pageable) {
        Page<Routine> page = (q == null || q.isBlank())
                ? routineRepository.findAll(pageable)
                : routineRepository.findByTitleContainingIgnoreCase(q.trim(), pageable);
        return page.map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineDTO> listByUserId(Long userId, Pageable pageable) {
        return routineRepository.findByUserId(userId, pageable).map(this::convertToDto);
    }
}
