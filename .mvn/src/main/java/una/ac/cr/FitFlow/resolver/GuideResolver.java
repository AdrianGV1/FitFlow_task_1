package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.GuideDTO;
import una.ac.cr.FitFlow.dto.GuidePageDTO;
import una.ac.cr.FitFlow.service.Guide.GuideService;
import una.ac.cr.FitFlow.dto.HabitDTO;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class GuideResolver {
    private final GuideService guideResolverService;
    private final HabitService habitService;

    @QueryMapping(name = "guideById")
    public GuideDTO guideById(@Argument("id") Long id) {
        return guideResolverService.findGuideById(id);
    }

    @QueryMapping(name = "guides")
    public GuidePageDTO guides(@Argument("page") int page, @Argument("size") int size,
            @Argument("keyword") String keyword) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<GuideDTO> pageResult = guideResolverService.listGuides(keyword, pageable);
        return GuidePageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @MutationMapping(name = "createGuide")
    public GuideDTO createGuide(@Argument("input") GuideDTO guide) {
        return guideResolverService.createGuide(guide);
    }

    @MutationMapping(name = "updateGuide")
    public GuideDTO updateGuide(@Argument("id") Long id, @Argument("input") GuideDTO guide) {
        return guideResolverService.updateGuide(id, guide);
    }

    @MutationMapping(name = "deleteGuide")
    public Boolean deleteGuide(@Argument("id") Long id) {
        guideResolverService.deleteGuide(id);
        return true;
    }

    @SchemaMapping(typeName = "Guide")
    public List<HabitDTO> recommendedHabits(GuideDTO guide) {
        if (guide.getRecommendedHabitIds() == null) {
            return List.of();
        }
        return guide.getRecommendedHabitIds().stream()
                .map(habitService::findHabitById)
                .collect(Collectors.toList());
    }
}

