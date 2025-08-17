package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.RoutineDTO;
import una.ac.cr.FitFlow.service.Routine.RoutineService;

@Controller
@RequiredArgsConstructor
public class RoutineResolver {
    private final RoutineService routineService;

    @QueryMapping
    public RoutineDTO getRoutineById(@Argument Long id) {
        return routineService.findById(id);
    }

    @QueryMapping
    public Page<RoutineDTO> getRoutines(@Argument int page, @Argument int size, @Argument String keyword) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return routineService.list(keyword, pageable);
    }

    @MutationMapping
    public RoutineDTO createRoutine(@Argument RoutineDTO routineDTO) {
        return routineService.create(routineDTO);
    }

    @MutationMapping
    public RoutineDTO updateRoutine(@Argument Long id, @Argument RoutineDTO routineDTO) {
        return routineService.update(id, routineDTO);
    }

    @MutationMapping
    public void deleteRoutine(@Argument Long id) {
        routineService.delete(id);
    }
}
