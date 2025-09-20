package una.ac.cr.FitFlow.dto.ProgressLog;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressLogOutputDTO {

    private Long id;
    private Long userId;
    private Long routineId;
    private LocalDateTime date;
    private List<Long> completedActivityIds;
}
