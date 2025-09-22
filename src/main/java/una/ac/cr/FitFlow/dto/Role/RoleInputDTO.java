package una.ac.cr.FitFlow.dto.Role;

import jakarta.validation.constraints.*;
import lombok.*;
import una.ac.cr.FitFlow.model.Role;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleInputDTO {

    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name; 

    @NotNull
    private Role.Permission permissions; 

    @NotNull
    private Role.Module module;          
}
