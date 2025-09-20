package una.ac.cr.FitFlow.dto.Reminder;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReminderOutputDTO {
    private Long id;
    private Long userId;
    private Long habitId;
    private String message;
    private LocalDateTime time; 
    private String frequency;
}
