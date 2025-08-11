package una.ac.cr.FitFlow.dto;

import java.time.LocalTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineActivityDTO {

    private Long id;

    @NotNull
    private Long routineId;

    @NotNull
    private Long habitId;

    @NotNull
    @Min(1)
    private Integer duration; 

    private LocalTime targetTime;

    @Size(max = 500)
    private String notes;
}

