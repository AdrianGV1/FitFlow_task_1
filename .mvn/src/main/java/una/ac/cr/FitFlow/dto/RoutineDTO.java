package una.ac.cr.FitFlow.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineDTO {

    public interface OnCreate {}
    public interface OnUpdate {}

    @NotNull(groups = OnUpdate.class)
    private Long id;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 150, groups = {OnCreate.class, OnUpdate.class})
    private String title;

    @NotNull(groups = OnCreate.class)
    @Positive(groups = {OnCreate.class, OnUpdate.class})
    private Long userId;

    @NotEmpty(groups = OnCreate.class)
    private Set<@NotBlank String> daysOfWeek;

    @NotEmpty(groups = OnCreate.class)
    private List<@NotNull @Positive Long> activityIds;
}
