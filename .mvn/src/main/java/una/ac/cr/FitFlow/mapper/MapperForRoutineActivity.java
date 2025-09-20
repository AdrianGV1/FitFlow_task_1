package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityInputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.RoutineActivity;

@Component
public class MapperForRoutineActivity {

    public RoutineActivityOutputDTO toDto(RoutineActivity ra) {
        if (ra == null) return null;
        return RoutineActivityOutputDTO.builder()
                .id(ra.getId())
                .routineId(ra.getRoutine() != null ? ra.getRoutine().getId() : null)
                .habitId(ra.getHabit() != null ? ra.getHabit().getId() : null)
                .duration(ra.getDuration())
                .notes(ra.getNotes())
                .build();
    }

    public RoutineActivity toEntity(RoutineActivityInputDTO in, Routine routine, Habit habit) {
        return RoutineActivity.builder()
                .routine(routine)
                .habit(habit)
                .duration(in.getDuration())
                .notes(in.getNotes())
                .build();
    }

    public void copyToEntity(RoutineActivityInputDTO in, RoutineActivity target,
                             Routine routineIfChanged, Habit habitIfChanged) {
        if (routineIfChanged != null) target.setRoutine(routineIfChanged);
        if (habitIfChanged != null)   target.setHabit(habitIfChanged);
        if (in.getDuration() != null) target.setDuration(in.getDuration());
        if (in.getNotes() != null)    target.setNotes(in.getNotes());
    }
}
