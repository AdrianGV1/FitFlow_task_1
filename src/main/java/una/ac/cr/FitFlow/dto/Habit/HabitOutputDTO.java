package una.ac.cr.FitFlow.dto.Habit;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HabitOutputDTO {

    private Long id;
    private String name;
    private String category;
    private String description;
}