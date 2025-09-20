package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.Routine.RoutineInputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.RoutineActivity;
import una.ac.cr.FitFlow.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MapperForRoutine {

    public String daysSetToCsv(Set<String> days) {
        if (days == null || days.isEmpty()) return "";
        return days.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .collect(Collectors.joining(","));
    }

    public Set<String> csvToDaysSet(String csv) {
        if (csv == null || csv.isBlank()) return Set.of();
        String[] parts = csv.split(",");
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String p : parts) {
            String v = p.trim();
            if (!v.isEmpty()) out.add(v);
        }
        return out;
    }


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
                .daysOfWeek(csvToDaysSet(r.getDaysOfWeek()))
                .activityIds(activityIds)
                .build();
    }

    public Routine toEntity(RoutineInputDTO in, User user) {
        return Routine.builder()
                .title(in.getTitle())
                .user(user)
                .daysOfWeek(daysSetToCsv(in.getDaysOfWeek()))
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
            target.setDaysOfWeek(daysSetToCsv(in.getDaysOfWeek()));
        }
    }
}
