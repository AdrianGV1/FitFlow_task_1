package una.ac.cr.FitFlow.resolver;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.ProgressLogDTO;
import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogService;

@Controller
@RequiredArgsConstructor
public class ProgressLogResolver {
    private final ProgressLogService progressLogService;

    @QueryMapping
    public ProgressLogDTO getProgressLogById(@Argument Long id) {
        return progressLogService.findById(id);
    }

    @QueryMapping
    public Page<ProgressLogDTO> getAllProgressLogs(@Argument int page, @Argument int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return progressLogService.list(pageable);
    }

    @QueryMapping
    public Page<ProgressLogDTO> getProgressLogsByUserId(@Argument Long userId, @Argument int page, @Argument int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return progressLogService.listByUserId(userId, pageable);
    }

    @QueryMapping
    public List<ProgressLogDTO> getProgressLogsByUserIdAndDate(@Argument Long userId, @Argument LocalDate date) {
        return progressLogService.findByUserIdAndDate(userId, date);
    }

    @MutationMapping
    public ProgressLogDTO createProgressLog(@Argument ProgressLogDTO dto) {
        return progressLogService.create(dto);
    }

    @MutationMapping
    public ProgressLogDTO updateProgressLog(@Argument Long id, @Argument ProgressLogDTO dto) {
        return progressLogService.update(id, dto);
    }

    @MutationMapping
    public void deleteProgressLog(@Argument Long id) {
        progressLogService.delete(id);
    }

}
