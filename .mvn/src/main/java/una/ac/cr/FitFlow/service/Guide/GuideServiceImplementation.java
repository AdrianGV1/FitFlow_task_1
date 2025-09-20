package una.ac.cr.FitFlow.service.Guide;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.GuideDTO;
import una.ac.cr.FitFlow.model.Guide;
import una.ac.cr.FitFlow.repository.GuideRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuideServiceImplementation implements GuideService {
    private final GuideRepository guideRepository;

    private GuideDTO convertToDto(Guide guide) {
        return GuideDTO.builder()
                .id(guide.getId())
                .title(guide.getTitle())
                .content(guide.getContent())
                .build();
    }

    private Guide convertToEntity(GuideDTO guideDTO) {
        return Guide.builder()
                .id(guideDTO.getId())
                .title(guideDTO.getTitle())
                .content(guideDTO.getContent())
                .build();
    }

    @Override
    @Transactional
    public GuideDTO createGuide(GuideDTO guideDTO) {
        Guide newGuide = convertToEntity(guideDTO);
        guideRepository.save(newGuide);
        return convertToDto(newGuide);
    }

    @Override
    @Transactional
    public GuideDTO updateGuide(Long id, GuideDTO guideDTO) {
        Guide existingGuide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guia no contada con el id: " + id));
        existingGuide.setTitle(guideDTO.getTitle());
        existingGuide.setContent(guideDTO.getContent());
        guideRepository.save(existingGuide);
        return convertToDto(existingGuide);
    }

    @Override
    @Transactional
    public void deleteGuide(Long id) {
        if (guideRepository.existsById(id)) {
            guideRepository.deleteById(id);
        } else {
            throw new RuntimeException("Guia no contada con el id: " + id);
        }
    }

    @Override
    public GuideDTO findGuideById(Long id) {
        if (guideRepository.existsById(id)) {
            Guide guide = guideRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Guia no contada con el id: " + id));
            return convertToDto(guide);
        } else {
            throw new RuntimeException("Guia no contada con el id: " + id);
        }
    }

    @Override
    public Page<GuideDTO> listGuides(String q, Pageable pageable) {
        if (q != null && !q.isEmpty()) {
            return guideRepository.findByTitleContainingIgnoreCase(q, pageable)
                    .map(this::convertToDto);
        } else {
            return guideRepository.findAll(pageable)
                    .map(this::convertToDto);
        }
    }

}
