package una.ac.cr.FitFlow.dto.AuthToken;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthTokenOutputDTO {
    private String token;
    private LocalDateTime expiresAt; 
    private Long userId;
}
