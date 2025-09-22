package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.Routine.RoutineInputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.RoutineActivity;
import una.ac.cr.FitFlow.model.User;

import java.util.List;
import java.util.Objects;

@Component
public class MapperForRoutine {

    public RoutineOutputDTO toDto(Routine r) {
        List<Long> activityIds = (r.getActivities() == null) ? List.of()
                : r.getActivities().stream()
                    .map(RoutineActivity::getId)
                    .filter(Objects::nonNull)
                    .toList();

        return RoutineOutputDTO.builder()
                .id(r.getId())
                .title(r.getTitle())
                .userId(r.getUser() != null ? r.getUser().getId() : null)
                .daysOfWeek(r.getDaysOfWeek())        
                .activityIds(activityIds)
                .build();
    }

    public Routine toEntity(RoutineInputDTO in, User user) {
        return Routine.builder()
                .title(in.getTitle())
                .user(user)
                .daysOfWeek(in.getDaysOfWeek())      
                .build();
    }

    public void copyToEntity(RoutineInputDTO in, Routine target, User userIfChanged) {
        if (in.getTitle() != null) {
            target.setTitle(in.getTitle());
        }
        if (userIfChanged != null) {
            target.setUser(userIfChanged);
        }
        if (in.getDaysOfWeek() != null) {
            target.setDaysOfWeek(in.getDaysOfWeek()); 
        }
    }
}
