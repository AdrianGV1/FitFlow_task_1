package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.HabitDTO;
import una.ac.cr.FitFlow.dto.ProgressLogDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityInputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivityPageDTO;
import una.ac.cr.FitFlow.service.CompletedActivity.CompletedActivityService;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogService;
import una.ac.cr.FitFlow.service.user.UserService;

@Controller
@RequiredArgsConstructor
@Validated
public class CompletedActivityResolver {
    private final CompletedActivityService completedActivityResolverService;
    private final HabitService habitService;
    private final ProgressLogService progressLogService;
    private final UserService userService;

    @QueryMapping(name = "completedActivityById")
    public CompletedActivityOutputDTO completedActivityById(@Argument("id") Long id) {
        return completedActivityResolverService.findCompletedActivityById(id);
    }

    @QueryMapping(name = "completedActivities")
    public CompletedActivityPageDTO completedActivities(@Argument("page") int page,
            @Argument("size") int size, @Argument("keyword") String keyword) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<CompletedActivityOutputDTO> pageResult = completedActivityResolverService.listCompletedActivities(keyword, pageable);
        return CompletedActivityPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @QueryMapping(name = "completedActivitiesByUserId")
    public CompletedActivityPageDTO completedActivitiesByUserId(@Argument("page") int page,
            @Argument("size") int size, @Argument("userId") Long userId) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<CompletedActivityOutputDTO> pageResult = completedActivityResolverService.findCompletedActivitiesByUserId(userId, pageable);
        return CompletedActivityPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @QueryMapping(name = "completedActivitiesByProgressLogId")
    public CompletedActivityPageDTO completedActivitiesByProgressLogId(@Argument("page") int page,
            @Argument("size") int size, @Argument("progressLogId") Long progressLogId) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<CompletedActivityOutputDTO> pageResult = completedActivityResolverService.findByProgressLogId(progressLogId, pageable);
        return CompletedActivityPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @MutationMapping(name = "createCompletedActivity")
    public CompletedActivityOutputDTO createCompletedActivity(@Argument("input") CompletedActivityInputDTO completedActivity) {
        return completedActivityResolverService.createCompletedActivity(completedActivity);
    }

    @MutationMapping(name = "updateCompletedActivity")
    public CompletedActivityOutputDTO updateCompletedActivity(@Argument("id") Long id,
            @Argument("input") CompletedActivityInputDTO completedActivity) {
        return completedActivityResolverService.updateCompletedActivity(id, completedActivity);
    }

    @MutationMapping(name = "deleteCompletedActivity")
    public Boolean deleteCompletedActivity(@Argument("id") Long id) {
        completedActivityResolverService.deleteCompletedActivity(id);
        return true;
    }

    @SchemaMapping(typeName = "CompletedActivity")
    public HabitDTO habit(CompletedActivityOutputDTO completedActivity) {
        return habitService.findHabitById(completedActivity.getHabitId());
    }

    @SchemaMapping(typeName = "CompletedActivity")
    public UserOutputDTO user(CompletedActivityOutputDTO completedActivity) {

        ProgressLogDTO log = progressLogService.findById(completedActivity.getProgressLogId());
        return userService.findUserById(log.getUserId());
    }
}
