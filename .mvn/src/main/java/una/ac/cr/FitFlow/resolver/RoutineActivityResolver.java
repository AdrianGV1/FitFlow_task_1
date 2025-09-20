package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.RoutineActivityDTO;
import una.ac.cr.FitFlow.dto.RoutineActivityPageDTO;
import una.ac.cr.FitFlow.service.RoutineActivity.RoutineActivityService;
import una.ac.cr.FitFlow.dto.RoutineDTO;
import una.ac.cr.FitFlow.dto.HabitDTO;
import una.ac.cr.FitFlow.service.Routine.RoutineService;
import una.ac.cr.FitFlow.service.Habit.HabitService;

@Controller
@RequiredArgsConstructor
public class RoutineActivityResolver {
    private final RoutineActivityService routineActivityService;
    private final RoutineService routineService;
    private final HabitService habitService;

    @QueryMapping(name = "routineActivityById")
    public RoutineActivityDTO routineActivityById(@Argument("id") Long id) {
        return routineActivityService.findById(id);
    }

    @QueryMapping(name = "routineActivities")
    public RoutineActivityPageDTO routineActivities(@Argument("page") int page, @Argument("size") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<RoutineActivityDTO> pageResult = routineActivityService.list(pageable);
        return RoutineActivityPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @QueryMapping(name = "routineActivitiesByRoutineId")
    public RoutineActivityPageDTO routineActivitiesByRoutineId(@Argument("routineId") Long routineId,
            @Argument("page") int page, @Argument("size") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<RoutineActivityDTO> pageResult = routineActivityService.listByRoutineId(routineId, pageable);
        return RoutineActivityPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @QueryMapping(name = "routineActivitiesByHabitId")
    public RoutineActivityPageDTO routineActivitiesByHabitId(@Argument("habitId") Long habitId,
            @Argument("page") int page, @Argument("size") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<RoutineActivityDTO> pageResult = routineActivityService.listByHabitId(habitId, pageable);
        return RoutineActivityPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @MutationMapping(name = "createRoutineActivity")
    public RoutineActivityDTO createRoutineActivity(@Argument("input") RoutineActivityDTO routineActivityDTO) {
        return routineActivityService.create(routineActivityDTO);
    }

    @MutationMapping(name = "updateRoutineActivity")
    public RoutineActivityDTO updateRoutineActivity(@Argument("id") Long id,
            @Argument("input") RoutineActivityDTO routineActivityDTO) {
        return routineActivityService.update(id, routineActivityDTO);
    }

    @MutationMapping(name = "deleteRoutineActivity")
    public Boolean deleteRoutineActivity(@Argument("id") Long id) {
        routineActivityService.delete(id);
        return true;
    }

    @SchemaMapping(typeName = "RoutineActivity")
    public RoutineDTO routine(RoutineActivityDTO routineActivity) {
        return routineService.findById(routineActivity.getRoutineId());
    }

    @SchemaMapping(typeName = "RoutineActivity")
    public HabitDTO habit(RoutineActivityDTO routineActivity) {
        return habitService.findHabitById(routineActivity.getHabitId());
    }
}
