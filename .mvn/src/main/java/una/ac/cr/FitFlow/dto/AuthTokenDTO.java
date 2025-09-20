package una.ac.cr.FitFlow.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthTokenDTO {

    public interface OnCreate {}
    public interface OnUpdate {}

    @NotNull(groups = OnUpdate.class)
    private Long id;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 2048, groups = {OnCreate.class, OnUpdate.class})
    private String token;

    @NotNull(groups = OnCreate.class)
    @Future(groups = {OnCreate.class, OnUpdate.class})
    private LocalDateTime expiresAt;

    @NotNull(groups = OnCreate.class)
    @Positive(groups = {OnCreate.class, OnUpdate.class})
    private Long userId;
}



