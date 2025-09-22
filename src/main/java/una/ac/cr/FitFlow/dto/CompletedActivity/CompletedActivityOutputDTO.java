package una.ac.cr.FitFlow.dto.CompletedActivity;

import java.time.OffsetDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompletedActivityOutputDTO {

    private Long id;
    private OffsetDateTime completedAt;   
    private String notes;
    private Long progressLogId;
    private Long habitId;
}
