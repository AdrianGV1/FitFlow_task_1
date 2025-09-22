package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.model.CompletedActivity;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MapperForProgressLog {

  public ProgressLogOutputDTO toDto(ProgressLog pl) {
    List<Long> caIds = (pl.getCompletedActivities() == null)
        ? Collections.emptyList()
        : pl.getCompletedActivities().stream()
            .map(CompletedActivity::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    return ProgressLogOutputDTO.builder()
        .id(pl.getId())
        .userId(pl.getUser() != null ? pl.getUser().getId() : null)
        .routineId(pl.getRoutine() != null ? pl.getRoutine().getId() : null)
        .date(pl.getLogDate())                
        .completedActivityIds(caIds)
        .build();
  }

  public ProgressLog toEntity(ProgressLogInputDTO in, User user, Routine routine) {
    return ProgressLog.builder()
        .user(user)
        .routine(routine)
        .logDate(in.getDate())                
        .build();
  }

  public void copyToEntity(ProgressLogInputDTO in,
                           ProgressLog target,
                           User userIfChanged,
                           Routine routineIfChanged) {
    if (userIfChanged != null)   target.setUser(userIfChanged);
    if (routineIfChanged != null) target.setRoutine(routineIfChanged);
    if (in.getDate() != null)    target.setLogDate(in.getDate()); 
  }
}
