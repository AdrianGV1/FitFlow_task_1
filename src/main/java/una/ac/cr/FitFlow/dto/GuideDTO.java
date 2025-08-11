package una.ac.cr.FitFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideDTO {

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String category;

    @NotNull
    @NotEmpty
    private Set<Long> recommendedHabitIds;
}

