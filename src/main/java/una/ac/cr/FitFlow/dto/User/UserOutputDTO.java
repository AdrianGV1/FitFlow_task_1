package una.ac.cr.FitFlow.dto.User;

import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserOutputDTO {

    private Long id;
    private String username;
    private String email;
    private Set<Long> roleIds;
    private Set<Long> habitIds;
}