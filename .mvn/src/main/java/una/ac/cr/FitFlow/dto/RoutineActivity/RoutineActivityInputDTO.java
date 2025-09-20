package una.ac.cr.FitFlow.dto.RoutineActivity;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineActivityInputDTO {

    private Long id;

    @NotNull
    @Positive
    private Long routineId;

    @NotNull
    @Positive
    private Long habitId;

    @NotNull
    @Min(value = 1)
    private Integer duration;

    @NotNull
    private LocalTime targetTime;

    @Size(max = 500)
    private String notes;
}
