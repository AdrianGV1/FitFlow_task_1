package una.ac.cr.FitFlow.dto.RoleDTO;

import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleOutputDTO {

    private Long id;
    private String name;
    private Set<String> permissions;
}
