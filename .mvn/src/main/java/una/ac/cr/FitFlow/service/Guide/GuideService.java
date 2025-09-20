package una.ac.cr.FitFlow.service.Guide;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.GuideDTO;

public interface GuideService {
    GuideDTO createGuide(GuideDTO guideDTO);
    GuideDTO updateGuide(Long id, GuideDTO guideDTO);
    void deleteGuide(Long id);
    GuideDTO findGuideById(Long id);
    Page<GuideDTO> listGuides(String q, Pageable pageable);
}
