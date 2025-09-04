package una.ac.cr.FitFlow.dto.CompletedActivityDTO;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompletedActivityInputDTO {

    private Long id;

    @NotNull
    private LocalDateTime completedAt;

    @Size(max = 500)
    private String notes;

    @NotNull
    @Positive
    private Long progressLogId;

    @NotNull
    @Positive
    private Long habitId;
}
