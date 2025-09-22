package una.ac.cr.FitFlow.dto.Routine;

import jakarta.validation.constraints.*;
import lombok.*;
import una.ac.cr.FitFlow.model.Routine.DaysOfWeek;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineInputDTO {

    private Long id; 

    @Size(max = 150)
    private String title;              

    @Positive
    private Long userId;               

    @NotNull 
    private List<DaysOfWeek> daysOfWeek;

    private List<@NotNull @Positive Long> activityIds; 
}
