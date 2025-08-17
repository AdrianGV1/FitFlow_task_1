package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.GuideDTO;
import una.ac.cr.FitFlow.service.Guide.GuideService;

@Controller
@RequiredArgsConstructor
public class GuideResolver {
    private final GuideService guideResolverService;

    @QueryMapping
    public GuideDTO getGuideById(@Argument Long id) {
        return guideResolverService.findGuideById(id);
    }

    @QueryMapping
    public Page<GuideDTO> getGuidesDto(@Argument int page, @Argument int size, @Argument String keyword) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return guideResolverService.listGuides(keyword, pageable);
    }

    @MutationMapping
    public GuideDTO createGuide(@Argument GuideDTO guide) {
        return guideResolverService.createGuide(guide);
    }

    @MutationMapping
    public GuideDTO updateGuide(@Argument Long id, @Argument GuideDTO guide) {
        return guideResolverService.updateGuide(id, guide);
    }

    @MutationMapping
    public void deleteGuide(@Argument Long id) {
        guideResolverService.deleteGuide(id);
    }
}
