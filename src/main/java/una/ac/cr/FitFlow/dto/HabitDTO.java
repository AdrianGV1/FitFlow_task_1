package una.ac.cr.FitFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitDTO {

    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    private String category;

    @Size(max = 500)
    private String description;
}

