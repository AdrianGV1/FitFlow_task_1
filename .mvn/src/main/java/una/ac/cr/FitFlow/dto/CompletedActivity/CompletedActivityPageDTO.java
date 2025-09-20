package una.ac.cr.FitFlow.dto.CompletedActivity;

import java.util.List;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompletedActivityPageDTO {
    private List<CompletedActivityOutputDTO> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
