package una.ac.cr.FitFlow.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthTokenDTO {

    @NotBlank
    private String token;

    @NotNull
    @Future
    private LocalDateTime expiresAt;

    @NotNull
    private Long userId;
}


