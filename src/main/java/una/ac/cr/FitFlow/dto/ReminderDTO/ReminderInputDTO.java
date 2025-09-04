package una.ac.cr.FitFlow.dto.ReminderDTO;

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

    @NotNull
    @FutureOrPresent
    private LocalDateTime time;

    @NotBlank
    @Size(max = 20)
    private String frequency;
}
