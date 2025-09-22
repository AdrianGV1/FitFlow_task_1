package una.ac.cr.FitFlow.dto.ProgressLog;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressLogOutputDTO {
    private Long id;
    private Long userId;
    private Long routineId;
    private OffsetDateTime date;   // <-- aquí
    private List<Long> completedActivityIds;
}
