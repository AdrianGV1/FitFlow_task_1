package una.ac.cr.FitFlow.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HabitDTO {

    public interface OnCreate {}
    public interface OnUpdate {}

    @NotNull(groups = OnUpdate.class)
    private Long id;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 100, groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 20, groups = {OnCreate.class, OnUpdate.class})
    private String category;

    @Size(max = 500, groups = {OnCreate.class, OnUpdate.class})
    private String description;
}

