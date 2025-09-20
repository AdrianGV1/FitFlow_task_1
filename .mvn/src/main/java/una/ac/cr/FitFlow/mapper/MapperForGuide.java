package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.Guide.GuideInputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;
import una.ac.cr.FitFlow.model.Guide;

import java.util.stream.Collectors;

@Component
public class MapperForGuide {

    public GuideOutputDTO toDto(Guide g) {
        return GuideOutputDTO.builder()
                .id(g.getId())
                .title(g.getTitle())
                .content(g.getContent())
                .category(g.getCategory() != null ? g.getCategory().name() : null)
                .recommendedHabitIds(
                        g.getRecommendedHabits().stream()
                                .map(h -> h.getId())
                                .collect(Collectors.toSet())
                )
                .build();
    }

    public Guide toEntity(GuideInputDTO in) {
        if (in == null) return null;
        return Guide.builder()
                .title(in.getTitle())
                .content(in.getContent())
                .category(parseCategory(in.getCategory()))
                .build();
    }

    public void copyToEntity(GuideInputDTO in, Guide target) {
        if (in.getTitle()   != null) target.setTitle(in.getTitle());
        if (in.getContent() != null) target.setContent(in.getContent());
        if (in.getCategory()!= null) target.setCategory(parseCategory(in.getCategory()));
    }

    private Guide.Category parseCategory(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        try {
            return Guide.Category.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                "Categoría inválida: " + raw + ". Use PHYSICAL, MENTAL, SLEEP o DIET."
            );
        }
    }
}
