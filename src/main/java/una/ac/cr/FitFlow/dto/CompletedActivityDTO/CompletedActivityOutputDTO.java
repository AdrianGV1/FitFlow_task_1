package una.ac.cr.FitFlow.dto.CompletedActivityDTO;

import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompletedActivityOutputDTO {

    private Long id;
    private LocalDateTime completedAt;
    private String notes;
    private Long progressLogId;
    private Long habitId;
}
