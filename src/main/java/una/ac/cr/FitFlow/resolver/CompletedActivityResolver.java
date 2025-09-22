package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.graphql.data.method.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.validation.Valid;

import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityInputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityPageDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.CompletedActivity.CompletedActivityService;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogService;
import una.ac.cr.FitFlow.service.user.UserService;

@Controller
@RequiredArgsConstructor
@Validated
public class CompletedActivityResolver {

    private static final Role.Module MODULE = Role.Module.PROGRESO;

    private final CompletedActivityService completedActivityService;
    private final HabitService habitService;
    private final ProgressLogService progressLogService;
    private final UserService userService;

    @QueryMapping(name = "completedActivityById")
    public CompletedActivityOutputDTO completedActivityById(@Argument("id") Long id) {
        SecurityUtils.requireRead(MODULE);
        return completedActivityService.findCompletedActivityById(id);
    }

    @QueryMapping(name = "completedActivities")
    public CompletedActivityPageDTO completedActivities(@Argument("page") int page,
                                                        @Argument("size") int size,
                                                        @Argument("keyword") String keyword) {
        SecurityUtils.requireRead(MODULE);
        Pageable pageable = PageRequest.of(page, size);
        Page<CompletedActivityOutputDTO> p = completedActivityService.listCompletedActivities(keyword, pageable);
        return CompletedActivityPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @QueryMapping(name = "completedActivitiesByUserId")
    public CompletedActivityPageDTO completedActivitiesByUserId(@Argument("page") int page,
                                                                @Argument("size") int size,
                                                                @Argument("userId") Long userId) {
        SecurityUtils.requireRead(MODULE);
        Pageable pageable = PageRequest.of(page, size);
        Page<CompletedActivityOutputDTO> p = completedActivityService.findCompletedActivitiesByUserId(userId, pageable);
        return CompletedActivityPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @QueryMapping(name = "completedActivitiesByProgressLogId")
    public CompletedActivityPageDTO completedActivitiesByProgressLogId(@Argument("page") int page,
                                                                       @Argument("size") int size,
                                                                       @Argument("progressLogId") Long progressLogId) {
        SecurityUtils.requireRead(MODULE);
        Pageable pageable = PageRequest.of(page, size);
        Page<CompletedActivityOutputDTO> p = completedActivityService.findByProgressLogId(progressLogId, pageable);
        return CompletedActivityPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @MutationMapping(name = "createCompletedActivity")
    public CompletedActivityOutputDTO createCompletedActivity(@Valid @Argument("input") CompletedActivityInputDTO input) {
        SecurityUtils.requireWrite(MODULE);
        return completedActivityService.createCompletedActivity(input);
    }

    @MutationMapping(name = "updateCompletedActivity")
    public CompletedActivityOutputDTO updateCompletedActivity(@Argument("id") Long id,
                                                              @Argument("input") CompletedActivityInputDTO input) {
        SecurityUtils.requireWrite(MODULE);
        return completedActivityService.updateCompletedActivity(id, input);
    }

    @MutationMapping(name = "deleteCompletedActivity")
    public Boolean deleteCompletedActivity(@Argument("id") Long id) {
        SecurityUtils.requireWrite(MODULE);
        completedActivityService.deleteCompletedActivity(id);
        return true;
    }

    @SchemaMapping(typeName = "CompletedActivity", field = "habit")
    public HabitOutputDTO habit(CompletedActivityOutputDTO ca) {
        return habitService.findHabitById(ca.getHabitId());
    }

    /*@SchemaMapping(typeName = "CompletedActivity", field = "user")
    public UserOutputDTO user(CompletedActivityOutputDTO ca) {
        ProgressLogOutputDTO log = progressLogService.findById(ca.getProgressLogId());
        return userService.findUserById(log.getUserId());
    }*/

    @QueryMapping(name = "monthlyCompletedActivitiesByCategoryAndDate")
public List<CompletedActivityOutputDTO> monthlyCompletedActivitiesByCategoryAndDate(
        @Argument String category,
        @Argument OffsetDateTime date) {
    SecurityUtils.requireRead(MODULE);
    return completedActivityService.monthlyCompletedActivitiesByCategoryAndDate(category, date);
}


}
