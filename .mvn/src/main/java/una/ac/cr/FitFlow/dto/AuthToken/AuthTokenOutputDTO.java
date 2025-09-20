package una.ac.cr.FitFlow.dto.AuthToken;

import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthTokenOutputDTO {

    private Long id;
    private String token;
    private LocalDateTime expiresAt;
    private Long userId;
}
