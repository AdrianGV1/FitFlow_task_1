package una.ac.cr.FitFlow.dto.RoutineActivity;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineActivityInputDTO {

    private Long id; 

    @NotNull @Positive
    private Long routineId;

    @NotNull @Positive
    private Long habitId;

    @NotNull @Min(1)
    private Integer duration;

    @Size(max = 500)
    private String notes; 
}
