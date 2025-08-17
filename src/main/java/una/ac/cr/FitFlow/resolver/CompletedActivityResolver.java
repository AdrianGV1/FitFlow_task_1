package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.CompletedActivityDTO;
import una.ac.cr.FitFlow.service.CompletedActivity.CompletedActivityService;

@Controller
@RequiredArgsConstructor
@Validated
public class CompletedActivityResolver {
    private final CompletedActivityService completedActivityResolverService;

    @QueryMapping
    public CompletedActivityDTO getCompletedActivityById(@Argument Long id) {
        return completedActivityResolverService.findCompletedActivityById(id);
    }

    @QueryMapping
    public Page<CompletedActivityDTO> listCompletedActivities(@Argument int page, @Argument int size,
            @Argument String keyword) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return completedActivityResolverService.listCompletedActivities(keyword, pageable);
    }

    @QueryMapping
    public Page<CompletedActivityDTO> listCompletedActivitiesByUserId(@Argument int page, @Argument int size,
            @Argument Long userId) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return completedActivityResolverService.findCompletedActivitiesByUserId(userId, pageable);
    }

    @QueryMapping
    public Page<CompletedActivityDTO> listCompletedActivitiesByProgressId(@Argument int page, @Argument int size,
            @Argument Long progressId) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return completedActivityResolverService.findByProgressLogId(progressId, pageable);
    }

    @MutationMapping
    public CompletedActivityDTO createCompletedActivity(@Argument CompletedActivityDTO completedActivity) {
        return completedActivityResolverService.createCompletedActivity(completedActivity);
    }

    @MutationMapping
    public CompletedActivityDTO updateCompletedActivity(@Argument Long id,
            @Argument CompletedActivityDTO completedActivity) {
        return completedActivityResolverService.updateCompletedActivity(id, completedActivity);
    }

    @MutationMapping
    public void deleteCompletedActivity(@Argument Long id) {
        completedActivityResolverService.deleteCompletedActivity(id);
    }
}
