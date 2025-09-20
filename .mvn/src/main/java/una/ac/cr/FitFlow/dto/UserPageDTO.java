package una.ac.cr.FitFlow.dto;

import java.util.List;
import lombok.*;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPageDTO {
    private List<UserOutputDTO> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
