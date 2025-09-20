package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.RoutineDTO;
import una.ac.cr.FitFlow.dto.RoutinePageDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.service.Routine.RoutineService;
import una.ac.cr.FitFlow.dto.RoutineActivityDTO;
import una.ac.cr.FitFlow.service.user.UserService;
import una.ac.cr.FitFlow.service.RoutineActivity.RoutineActivityService;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class RoutineResolver {
    private final RoutineService routineService;
    private final UserService userService;
    private final RoutineActivityService routineActivityService;

    @QueryMapping(name = "routineById")
    public RoutineDTO routineById(@Argument("id") Long id) {
        return routineService.findById(id);
    }

    @QueryMapping(name = "routines")
    public RoutinePageDTO routines(@Argument("page") int page, @Argument("size") int size,
            @Argument("keyword") String keyword) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<RoutineDTO> pageResult = routineService.list(keyword, pageable);
        return RoutinePageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @MutationMapping(name = "createRoutine")
    public RoutineDTO createRoutine(@Argument("input") RoutineDTO routineDTO) {
        return routineService.create(routineDTO);
    }

    @MutationMapping(name = "updateRoutine")
    public RoutineDTO updateRoutine(@Argument("id") Long id, @Argument("input") RoutineDTO routineDTO) {
        return routineService.update(id, routineDTO);
    }

    @MutationMapping(name = "deleteRoutine")
    public Boolean deleteRoutine(@Argument("id") Long id) {
        routineService.delete(id);
        return true;
    }

    @SchemaMapping(typeName = "Routine")
    public UserOutputDTO user(RoutineDTO routine) {
        return userService.findUserById(routine.getUserId());
    }

    @SchemaMapping(typeName = "Routine")
    public List<RoutineActivityDTO> activities(RoutineDTO routine) {
        if (routine.getActivityIds() == null) {
            return List.of();
        }
        return routine.getActivityIds().stream()
                .map(routineActivityService::findById)
                .collect(Collectors.toList());
    }
}

