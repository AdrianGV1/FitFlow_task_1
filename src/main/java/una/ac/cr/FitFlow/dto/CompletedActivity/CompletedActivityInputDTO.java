package una.ac.cr.FitFlow.dto.CompletedActivity;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompletedActivityInputDTO {

    private Long id;

    @NotNull
    private OffsetDateTime completedAt; 

    @Size(max = 500)
    private String notes;

    @NotNull @Positive
    private Long progressLogId;

    @NotNull @Positive
    private Long habitId;
}
