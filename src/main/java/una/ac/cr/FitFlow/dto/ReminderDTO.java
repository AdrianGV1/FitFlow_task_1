package una.ac.cr.FitFlow.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderDTO {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long habitId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime time;

    @NotBlank
    private String frequency;
}

