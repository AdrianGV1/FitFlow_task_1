package una.ac.cr.FitFlow.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedActivityDTO {

    private Long id;

    @NotNull
    private LocalDateTime completedAt;

    @Size(max = 500)
    private String notes;

    @NotNull
    private Long progressLogId;

    @NotNull
    private Long habitId;
}


