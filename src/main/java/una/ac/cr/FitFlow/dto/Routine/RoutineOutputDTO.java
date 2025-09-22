package una.ac.cr.FitFlow.dto.Routine;

import lombok.*;
import una.ac.cr.FitFlow.model.Routine.DaysOfWeek;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineOutputDTO {
    private Long id;
    private String title;
    private Long userId;
    private List<DaysOfWeek> daysOfWeek;
    private List<Long> activityIds;  
}
