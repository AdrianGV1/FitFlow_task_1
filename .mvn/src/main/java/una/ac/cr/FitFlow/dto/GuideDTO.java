package una.ac.cr.FitFlow.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuideDTO {

    public interface OnCreate {}
    public interface OnUpdate {}

    @NotNull(groups = OnUpdate.class)
    private Long id;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 150, groups = {OnCreate.class, OnUpdate.class})
    private String title;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 10000, groups = {OnCreate.class, OnUpdate.class})
    private String content;
    
    @NotBlank(groups = OnCreate.class)
    @Size(max = 20, groups = {OnCreate.class, OnUpdate.class})
    private String category;

    @NotEmpty(groups = OnCreate.class)
    private Set<@NotNull @Positive Long> recommendedHabitIds;
}


