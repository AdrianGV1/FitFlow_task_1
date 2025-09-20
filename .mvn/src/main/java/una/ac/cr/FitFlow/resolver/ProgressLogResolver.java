package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogPageDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;

import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogService;
import una.ac.cr.FitFlow.service.user.UserService;
import una.ac.cr.FitFlow.service.Routine.RoutineService;
import una.ac.cr.FitFlow.service.CompletedActivity.CompletedActivityService;

@Controller
@RequiredArgsConstructor
public class ProgressLogResolver {

    private final ProgressLogService progressLogService;
    private final UserService userService;
    private final RoutineService routineService;
    private final CompletedActivityService completedActivityService;

    @QueryMapping(name = "progressLogById")
    public ProgressLogOutputDTO progressLogById(@Argument("id") Long id) {
        return progressLogService.findById(id);
    }

    @QueryMapping(name = "progressLogs")
    public ProgressLogPageDTO progressLogs(@Argument("page") int page, @Argument("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProgressLogOutputDTO> p = progressLogService.list(pageable);
        return ProgressLogPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @QueryMapping(name = "progressLogsByUserId")
    public ProgressLogPageDTO progressLogsByUserId(@Argument("userId") Long userId,
                                                   @Argument("page") int page,
                                                   @Argument("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProgressLogOutputDTO> p = progressLogService.listByUserId(userId, pageable);
        return ProgressLogPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @QueryMapping(name = "progressLogsByUserIdAndDate")
    public List<ProgressLogOutputDTO> progressLogsByUserIdAndDate(@Argument("userId") Long userId,
                                                                  @Argument("date") LocalDate date) {
        return progressLogService.findByUserIdAndDate(userId, date);
    }

    @MutationMapping(name = "createProgressLog")
    public ProgressLogOutputDTO createProgressLog(@Argument("input") ProgressLogInputDTO input) {
        return progressLogService.create(input);
    }

    @MutationMapping(name = "updateProgressLog")
    public ProgressLogOutputDTO updateProgressLog(@Argument("id") Long id,
                                                  @Argument("input") ProgressLogInputDTO input) {
        return progressLogService.update(id, input);
    }

    @MutationMapping(name = "deleteProgressLog")
    public Boolean deleteProgressLog(@Argument("id") Long id) {
        progressLogService.delete(id);
        return true;
    }


    @SchemaMapping(typeName = "ProgressLog", field = "user")
    public UserOutputDTO user(ProgressLogOutputDTO log) {
        return userService.findUserById(log.getUserId());
    }

    @SchemaMapping(typeName = "ProgressLog", field = "routine")
    public RoutineOutputDTO routine(ProgressLogOutputDTO log) {
        return routineService.findById(log.getRoutineId());
    }

    @SchemaMapping(typeName = "ProgressLog", field = "completedActivities")
    public List<CompletedActivityOutputDTO> completedActivities(ProgressLogOutputDTO log) {
        if (log.getCompletedActivityIds() == null || log.getCompletedActivityIds().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        return log.getCompletedActivityIds().stream()
                .map(id -> completedActivityService.findCompletedActivityById(id))
                .collect(Collectors.toList());
    }
}
