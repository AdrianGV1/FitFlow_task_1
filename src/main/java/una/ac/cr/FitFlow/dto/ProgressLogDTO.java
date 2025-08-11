package una.ac.cr.FitFlow.dto;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressLogDTO {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long routineId;

    @NotNull
    private LocalDateTime date;

    @NotEmpty
    private List<Long> completedActivityIds;
}

