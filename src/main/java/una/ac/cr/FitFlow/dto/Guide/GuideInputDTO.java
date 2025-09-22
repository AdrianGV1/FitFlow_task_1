package una.ac.cr.FitFlow.dto.Guide;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuideInputDTO {

    private Long id;

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    @Size(max = 10000)
    private String content;
    
    @NotBlank
    @Size(max = 20)
    private String category;

    @NotEmpty
    private Set<@NotNull @Positive Long> recommendedHabitIds;
}
