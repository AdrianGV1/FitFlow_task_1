package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.RoutineActivityDTO;
import una.ac.cr.FitFlow.service.RoutineActivity.RoutineActivityService;

@Controller
@RequiredArgsConstructor
public class RoutineActivityResolver {
    private final RoutineActivityService routineActivityService;

    @QueryMapping
    public RoutineActivityDTO getRoutineActivityById(@Argument Long id) {
        return routineActivityService.findById(id);
    }

    @QueryMapping
    public Page<RoutineActivityDTO> getAllRoutineActivities(@Argument int page, @Argument int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return routineActivityService.list(pageable);
    }

    @QueryMapping
    public Page<RoutineActivityDTO> getRoutineActivitiesByRoutineId(@Argument Long routineId, @Argument int page,
            @Argument int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return routineActivityService.listByRoutineId(routineId, pageable);
    }

    @QueryMapping
    public Page<RoutineActivityDTO> getRoutineActivitiesByHabidId(@Argument Long habidId, @Argument int page,
            @Argument int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return routineActivityService.listByHabitId(habidId, pageable);
    }

    @MutationMapping
    public RoutineActivityDTO createRoutineActivity(@Argument RoutineActivityDTO routineActivityDTO) {
        return routineActivityService.create(routineActivityDTO);
    }

    @MutationMapping
    public RoutineActivityDTO updateRoutineActivity(@Argument Long id,
            @Argument RoutineActivityDTO routineActivityDTO) {
        return routineActivityService.update(id, routineActivityDTO);
    }

    @MutationMapping
    public void deleteRoutineActivity(@Argument Long id) {
        routineActivityService.delete(id);
    }
}