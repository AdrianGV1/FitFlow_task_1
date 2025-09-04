package una.ac.cr.FitFlow.dto.ReminderDTO;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReminderOutputDTO {

    private Long id;
    private Long userId;
    private Long habitId;
    private LocalDateTime time;
    private String frequency;
}
