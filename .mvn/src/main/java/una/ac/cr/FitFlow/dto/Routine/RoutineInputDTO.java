package una.ac.cr.FitFlow.dto.Routine;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineInputDTO {

    private Long id;

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotNull
    @Positive
    private Long userId;

    @NotEmpty
    private Set<@NotBlank String> daysOfWeek;

    @NotEmpty
    private List<@NotNull @Positive Long> activityIds;
}
