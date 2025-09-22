package una.ac.cr.FitFlow.dto.RoutineActivity;

import java.util.List;
import lombok.*;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineActivityPageDTO {
    private List<RoutineActivityOutputDTO> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
