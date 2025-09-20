package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityPageDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityInputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;

import una.ac.cr.FitFlow.service.RoutineActivity.RoutineActivityService;
import una.ac.cr.FitFlow.service.Routine.RoutineService;
import una.ac.cr.FitFlow.service.Habit.HabitService;

@Controller
@RequiredArgsConstructor
public class RoutineActivityResolver {

    private final RoutineActivityService routineActivityService;
    private final RoutineService routineService;
    private final HabitService habitService;


    @QueryMapping(name = "routineActivityById")
    public RoutineActivityOutputDTO routineActivityById(@Argument("id") Long id) {
        return routineActivityService.findById(id);
    }

    @QueryMapping(name = "routineActivities")
    public RoutineActivityPageDTO routineActivities(@Argument("page") int page,
                                                    @Argument("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoutineActivityOutputDTO> p = routineActivityService.list(pageable);
        return RoutineActivityPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @QueryMapping(name = "routineActivitiesByRoutineId")
    public RoutineActivityPageDTO routineActivitiesByRoutineId(@Argument("routineId") Long routineId,
                                                               @Argument("page") int page,
                                                               @Argument("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoutineActivityOutputDTO> p = routineActivityService.listByRoutineId(routineId, pageable);
        return RoutineActivityPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @QueryMapping(name = "routineActivitiesByHabitId")
    public RoutineActivityPageDTO routineActivitiesByHabitId(@Argument("habitId") Long habitId,
                                                             @Argument("page") int page,
                                                             @Argument("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoutineActivityOutputDTO> p = routineActivityService.listByHabitId(habitId, pageable);
        return RoutineActivityPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }


    @MutationMapping(name = "createRoutineActivity")
    public RoutineActivityOutputDTO createRoutineActivity(@Argument("input") RoutineActivityInputDTO input) {
        return routineActivityService.create(input);
    }

    @MutationMapping(name = "updateRoutineActivity")
    public RoutineActivityOutputDTO updateRoutineActivity(@Argument("id") Long id,
                                                          @Argument("input") RoutineActivityInputDTO input) {
        return routineActivityService.update(id, input);
    }

    @MutationMapping(name = "deleteRoutineActivity")
    public Boolean deleteRoutineActivity(@Argument("id") Long id) {
        routineActivityService.delete(id);
        return true;
    }


    @SchemaMapping(typeName = "RoutineActivity", field = "routine")
    public RoutineOutputDTO routine(RoutineActivityOutputDTO ra) {
        return routineService.findById(ra.getRoutineId());
    }

    @SchemaMapping(typeName = "RoutineActivity", field = "habit")
    public HabitOutputDTO habit(RoutineActivityOutputDTO ra) {
        return habitService.findHabitById(ra.getHabitId());
    }
}
