package una.ac.cr.FitFlow.dto.Reminder;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReminderOutputDTO {

    private Long id;
    private Long userId;
    private Long habitId;
    @NotBlank
    @Size(max = 255)
    private String message;
    private LocalDateTime time;
    private String frequency;
}
