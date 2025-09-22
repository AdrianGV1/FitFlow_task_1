package una.ac.cr.FitFlow.dto.RoutineActivity;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineActivityOutputDTO {
    private Long id;
    private Long routineId;
    private Long habitId;
    private Integer duration;
    private String notes;
}
