package una.ac.cr.FitFlow.service.Guide;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import una.ac.cr.FitFlow.dto.Guide.GuideInputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForGuide;
import una.ac.cr.FitFlow.model.Guide;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.repository.GuideRepository;
import una.ac.cr.FitFlow.repository.HabitRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuideServiceImplementation implements GuideService {

    private final GuideRepository guideRepository;
    private final HabitRepository habitRepository;
    private final MapperForGuide mapper;


    private Set<Habit> resolveHabits(Set<Long> ids) {
        if (ids == null) return null;          
        if (ids.isEmpty()) return new HashSet<>(); 
        List<Habit> all = habitRepository.findAllById(ids);
        if (all.size() != ids.size()) {
            throw new IllegalArgumentException("Uno o más recommendedHabitIds no existen.");
        }
        return new HashSet<>(all);
    }

    private GuideOutputDTO toDto(Guide g) {
        return mapper.toDto(g);
    }

    @Override
    @Transactional
    public GuideOutputDTO createGuide(GuideInputDTO in) {
        if (in.getTitle() == null || in.getTitle().isBlank())
            throw new IllegalArgumentException("El título es obligatorio.");
        if (in.getContent() == null || in.getContent().isBlank())
            throw new IllegalArgumentException("El contenido es obligatorio.");
        if (in.getCategory() == null || in.getCategory().isBlank())
            throw new IllegalArgumentException("La categoría es obligatoria.");

        Guide g = mapper.toEntity(in);
        Set<Habit> rec = resolveHabits(in.getRecommendedHabitIds());
        if (rec != null) g.setRecommendedHabits(rec);

        Guide saved = guideRepository.save(g);
        return toDto(saved);
    }

    @Override
    @Transactional
    public GuideOutputDTO updateGuide(Long id, GuideInputDTO in) {
        Guide g = guideRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Guía no encontrada: id=" + id));

        mapper.copyToEntity(in, g);

        Set<Habit> rec = resolveHabits(in.getRecommendedHabitIds());
        if (rec != null) {
            g.setRecommendedHabits(rec); 
        }

        Guide saved = guideRepository.save(g);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteGuide(Long id) {
        Guide g = guideRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Guía no encontrada: id=" + id));
        guideRepository.delete(g);
    }

    @Override
    public GuideOutputDTO findGuideById(Long id) {
        Guide g = guideRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Guía no encontrada: id=" + id));
        return toDto(g);
    }

    @Override
    public Page<GuideOutputDTO> listGuides(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return guideRepository.findAll(pageable).map(this::toDto);
        }
       
        try {
            Guide.Category cat = Guide.Category.valueOf(q.trim().toUpperCase());
            return guideRepository.findByCategory(cat, pageable).map(this::toDto);
        } catch (IllegalArgumentException ignored) {
            
            return guideRepository
                    .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q, pageable)
                    .map(this::toDto);
        }
    }
}
