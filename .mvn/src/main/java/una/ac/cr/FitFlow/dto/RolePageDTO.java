package una.ac.cr.FitFlow.dto;

import java.util.List;
import lombok.*;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePageDTO {
    private List<RoleOutputDTO> content;

    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
