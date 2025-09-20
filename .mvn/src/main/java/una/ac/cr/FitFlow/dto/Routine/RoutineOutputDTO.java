package una.ac.cr.FitFlow.dto.Routine;

import lombok.*;
import java.util.List;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineOutputDTO {
    private Long id;
    private String title;
    private Long userId;
    private Set<String> daysOfWeek;  
    private List<Long> activityIds;  
}
