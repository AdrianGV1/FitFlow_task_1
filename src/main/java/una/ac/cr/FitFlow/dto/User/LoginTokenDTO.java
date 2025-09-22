package una.ac.cr.FitFlow.dto.User;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginTokenDTO {
    private String token;
    private OffsetDateTime expiresAt;
    private Long userId;
}
