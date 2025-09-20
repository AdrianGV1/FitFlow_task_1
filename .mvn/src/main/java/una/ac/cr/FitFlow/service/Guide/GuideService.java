package una.ac.cr.FitFlow.service.Guide;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.Guide.GuideInputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;

public interface GuideService {
    GuideOutputDTO createGuide(GuideInputDTO input);
    GuideOutputDTO updateGuide(Long id, GuideInputDTO input);
    void deleteGuide(Long id);
    GuideOutputDTO findGuideById(Long id);
    Page<GuideOutputDTO> listGuides(String q, Pageable pageable);
}
