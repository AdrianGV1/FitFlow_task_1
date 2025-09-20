package una.ac.cr.FitFlow.dto.AuthToken;

import java.util.List;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthTokenPageDTO {
    private List<AuthTokenOutputDTO> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
