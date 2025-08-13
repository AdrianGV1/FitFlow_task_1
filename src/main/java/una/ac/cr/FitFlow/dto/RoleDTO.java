package una.ac.cr.FitFlow.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleDTO {

    public interface OnCreate {}
    public interface OnUpdate {}

    @NotNull(groups = OnUpdate.class)
    private Long id;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotEmpty(groups = OnCreate.class)
    private Set<@NotBlank String> permissions;
}
