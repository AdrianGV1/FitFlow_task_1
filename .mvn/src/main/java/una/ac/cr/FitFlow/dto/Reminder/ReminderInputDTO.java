package una.ac.cr.FitFlow.dto.Reminder;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReminderInputDTO {

    private Long id;

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Positive
    private Long habitId;

    @NotBlank
    @Size(max = 255)
    private String message;

    @NotNull
    private LocalDateTime time;

    @NotBlank
    @Size(max = 20)
    private String frequency;
}
