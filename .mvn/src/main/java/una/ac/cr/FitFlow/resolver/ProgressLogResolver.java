package una.ac.cr.FitFlow.resolver;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.ProgressLogDTO;
import una.ac.cr.FitFlow.dto.ProgressLogPageDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogService;
import una.ac.cr.FitFlow.service.user.UserService;

@Controller
@RequiredArgsConstructor
public class ProgressLogResolver {
    private final ProgressLogService progressLogService;
    private final UserService userService;

    @QueryMapping(name = "progressLogById")
    public ProgressLogDTO progressLogById(@Argument("id") Long id) {
        return progressLogService.findById(id);
    }

    @QueryMapping(name = "progressLogs")
    public ProgressLogPageDTO progressLogs(@Argument("page") int page, @Argument("size") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<ProgressLogDTO> pageResult = progressLogService.list(pageable);
        return ProgressLogPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @QueryMapping(name = "progressLogsByUserId")
    public ProgressLogPageDTO progressLogsByUserId(@Argument("userId") Long userId,
            @Argument("page") int page, @Argument("size") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<ProgressLogDTO> pageResult = progressLogService.listByUserId(userId, pageable);
        return ProgressLogPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @QueryMapping(name = "progressLogsByUserIdAndDate")
    public List<ProgressLogDTO> progressLogsByUserIdAndDate(@Argument("userId") Long userId,
            @Argument("date") LocalDate date) {
        return progressLogService.findByUserIdAndDate(userId, date);
    }

    @MutationMapping(name = "createProgressLog")
    public ProgressLogDTO createProgressLog(@Argument("input") ProgressLogDTO dto) {
        return progressLogService.create(dto);
    }

    @MutationMapping(name = "updateProgressLog")
    public ProgressLogDTO updateProgressLog(@Argument("id") Long id, @Argument("input") ProgressLogDTO dto) {
        return progressLogService.update(id, dto);
    }

    @MutationMapping(name = "deleteProgressLog")
    public Boolean deleteProgressLog(@Argument("id") Long id) {
        progressLogService.delete(id);
        return true;
    }

    @SchemaMapping(typeName = "ProgressLog")
    public UserOutputDTO user(ProgressLogDTO log) {
        return userService.findUserById(log.getUserId());
    }

}

