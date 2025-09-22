package una.ac.cr.FitFlow.dto.User;

import jakarta.validation.constraints.*;
import java.util.Set;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserInputDTO {
    private Long id;

    @NotBlank @Size(max = 50)
    private String username;

    @NotBlank @Size(min = 8, max = 100)
    private String password;

    @NotBlank @Email @Size(max = 100)
    private String email;

    @NotEmpty
    private Set<@NotNull @Positive Long> roleIds;  

    private Set<@NotNull @Positive Long> habitIds;  
}
