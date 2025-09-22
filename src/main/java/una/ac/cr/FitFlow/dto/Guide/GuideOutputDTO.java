package una.ac.cr.FitFlow.dto.Guide;

import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuideOutputDTO {

    private Long id;
    private String title;
    private String content;
    private String category;
    private Set<Long> recommendedHabitIds;
}
