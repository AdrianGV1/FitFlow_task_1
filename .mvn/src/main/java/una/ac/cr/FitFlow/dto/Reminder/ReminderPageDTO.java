package una.ac.cr.FitFlow.dto.Reminder;

import java.util.List;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReminderPageDTO {
    private List<ReminderOutputDTO> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
