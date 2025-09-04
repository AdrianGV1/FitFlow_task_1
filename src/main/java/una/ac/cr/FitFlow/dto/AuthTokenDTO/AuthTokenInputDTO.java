package una.ac.cr.FitFlow.dto.AuthTokenDTO;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthTokenInputDTO {

    private Long id;

    @NotBlank
    @Size(max = 2048)
    private String token;

    @NotNull
    @Future
    private LocalDateTime expiresAt;

    @NotNull
    @Positive
    private Long userId;
}
