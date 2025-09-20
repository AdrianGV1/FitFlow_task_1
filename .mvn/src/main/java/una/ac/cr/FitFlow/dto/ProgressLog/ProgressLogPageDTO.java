package una.ac.cr.FitFlow.dto.ProgressLog;

import java.util.List;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressLogPageDTO {
    private List<ProgressLogOutputDTO> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
