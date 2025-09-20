package una.ac.cr.FitFlow.dto.Role;

import lombok.*;
import una.ac.cr.FitFlow.model.Role;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleOutputDTO {
    private Long id;                     
    private String name;                 
    private Role.Permission permissions; 
    private Role.Module module;          
}
