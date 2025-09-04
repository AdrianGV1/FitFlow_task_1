package una.ac.cr.FitFlow.dto.RoleDTO;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleInputDTO {

    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotEmpty
    private Set<@NotBlank String> permissions;
}
