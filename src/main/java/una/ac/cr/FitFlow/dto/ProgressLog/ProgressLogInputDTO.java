package una.ac.cr.FitFlow.dto.ProgressLog;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressLogInputDTO {
    private Long id;

    @NotNull @Positive
    private Long userId;

    @NotNull @Positive
    private Long routineId;

    @NotNull @PastOrPresent
    private OffsetDateTime date;   

    private List<@NotNull @Positive Long> completedActivityIds;
}
