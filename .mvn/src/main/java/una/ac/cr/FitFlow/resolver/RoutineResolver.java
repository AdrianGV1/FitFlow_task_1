package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import una.ac.cr.FitFlow.dto.Routine.RoutineInputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutinePageDTO;

import una.ac.cr.FitFlow.service.Routine.RoutineService;
import una.ac.cr.FitFlow.service.user.UserService;
import una.ac.cr.FitFlow.service.RoutineActivity.RoutineActivityService;

@Controller
@RequiredArgsConstructor
public class RoutineResolver {

    private final RoutineService routineService;
    private final UserService userService;
    private final RoutineActivityService routineActivityService;



    @QueryMapping(name = "routineById")
    public RoutineOutputDTO routineById(@Argument("id") Long id) {
        return routineService.findById(id);
    }

    @QueryMapping(name = "routines")
    public RoutinePageDTO routines(@Argument("page") int page,
                                   @Argument("size") int size,
                                   @Argument("keyword") String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoutineOutputDTO> p = routineService.list(keyword, pageable);
        return RoutinePageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @QueryMapping(name = "routinesByUserId")
    public RoutinePageDTO routinesByUserId(@Argument("userId") Long userId,
                                           @Argument("page") int page,
                                           @Argument("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoutineOutputDTO> p = routineService.listByUserId(userId, pageable);
        return RoutinePageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }


    @MutationMapping(name = "createRoutine")
    public RoutineOutputDTO createRoutine(@Argument("input") RoutineInputDTO input) {
        return routineService.create(input);
    }

    @MutationMapping(name = "updateRoutine")
    public RoutineOutputDTO updateRoutine(@Argument("id") Long id,
                                          @Argument("input") RoutineInputDTO input) {
        return routineService.update(id, input);
    }

    @MutationMapping(name = "deleteRoutine")
    public Boolean deleteRoutine(@Argument("id") Long id) {
        routineService.delete(id);
        return true;
    }

    @SchemaMapping(typeName = "Routine", field = "user")
    public UserOutputDTO user(RoutineOutputDTO routine) {
        return userService.findUserById(routine.getUserId());
    }

    @SchemaMapping(typeName = "Routine", field = "activities")
    public List<RoutineActivityOutputDTO> activities(RoutineOutputDTO routine) {
        if (routine.getActivityIds() == null || routine.getActivityIds().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return routine.getActivityIds().stream()
                .map(id -> (RoutineActivityOutputDTO) routineActivityService.findById(id))
                .collect(Collectors.toList());
    }
}
