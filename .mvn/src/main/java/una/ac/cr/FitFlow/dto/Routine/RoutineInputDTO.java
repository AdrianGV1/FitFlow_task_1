package una.ac.cr.FitFlow.dto.Routine;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineInputDTO {

    private Long id; 

    @Size(max = 150)
    private String title;              

    @Positive
    private Long userId;               

    private Set<@NotBlank String> daysOfWeek; 

    private List<@NotNull @Positive Long> activityIds; 
}
