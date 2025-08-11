package una.ac.cr.FitFlow.dto;

import jakarta.validation.constraints.*;
import java.util.Set;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {

    public interface OnCreate {}
    public interface OnUpdate {}

    @NotNull(groups = OnUpdate.class)
    private Long id;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String username;

    @NotBlank(groups = OnCreate.class)
    @Size(min = 8, max = 100, groups = {OnCreate.class, OnUpdate.class})
    private String password;

    @NotBlank(groups = OnCreate.class)
    @Email(groups = {OnCreate.class, OnUpdate.class})
    @Size(max = 100, groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotEmpty(groups = OnCreate.class)
    private Set<@NotNull @Positive Long> roleIds;

    private Set<@NotNull @Positive Long> favoriteHabitIds;
}


