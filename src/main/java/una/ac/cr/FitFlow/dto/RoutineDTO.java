package una.ac.cr.FitFlow.dto;

import java.util.List;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineDTO {

    private Long id;

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotNull
    private Long userId;

    @NotEmpty
    private Set<String> daysOfWeek;

    @NotEmpty
    private List<Long> activityIds;
}

