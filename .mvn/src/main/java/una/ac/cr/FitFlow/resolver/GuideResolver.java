package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import una.ac.cr.FitFlow.dto.Guide.GuideInputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuidePageDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.service.Guide.GuideService;
import una.ac.cr.FitFlow.service.Habit.HabitService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class GuideResolver {

    private final GuideService guideService;
    private final HabitService habitService;

    @QueryMapping(name = "guideById")
    public GuideOutputDTO guideById(@Argument("id") Long id) {
        return guideService.findGuideById(id);
    }

    @QueryMapping(name = "guides")
    public GuidePageDTO guides(@Argument("page") int page,
                               @Argument("size") int size,
                               @Argument("keyword") String keyword) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<GuideOutputDTO> p = guideService.listGuides(keyword, pageable);
        return GuidePageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @MutationMapping(name = "createGuide")
    public GuideOutputDTO createGuide(@Argument("input") GuideInputDTO input) {
        return guideService.createGuide(input);
    }

    @MutationMapping(name = "updateGuide")
    public GuideOutputDTO updateGuide(@Argument("id") Long id,
                                      @Argument("input") GuideInputDTO input) {
        return guideService.updateGuide(id, input);
    }

    @MutationMapping(name = "deleteGuide")
    public Boolean deleteGuide(@Argument("id") Long id) {
        guideService.deleteGuide(id);
        return true;
    }

    @SchemaMapping(typeName = "Guide", field = "recommendedHabits")
    public List<HabitOutputDTO> recommendedHabits(GuideOutputDTO guide) {
        if (guide.getRecommendedHabitIds() == null || guide.getRecommendedHabitIds().isEmpty()) {
            return List.of();
        }
        return guide.getRecommendedHabitIds().stream()
                .map(habitService::findHabitById)
                .collect(Collectors.toList());
    }
}
