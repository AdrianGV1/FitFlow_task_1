package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import una.ac.cr.FitFlow.dto.Habit.HabitInputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitPageDTO;
import una.ac.cr.FitFlow.service.Habit.HabitService;

@Controller
@RequiredArgsConstructor
public class HabitResolver {

    private final HabitService habitService;

    @QueryMapping(name = "habitById")
    public HabitOutputDTO habitById(@Argument("id") Long id) {
        return habitService.findHabitById(id);
    }

    @QueryMapping(name = "habits")
    public HabitPageDTO habits(@Argument("page") int page,
                               @Argument("size") int size,
                               @Argument("keyword") String keyword) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<HabitOutputDTO> p = habitService.listHabits(keyword, pageable);
        return HabitPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @MutationMapping(name = "createHabit")
    public HabitOutputDTO createHabit(@Argument("input") HabitInputDTO input) {
        return habitService.createHabit(input);
    }

    @MutationMapping(name = "updateHabit")
    public HabitOutputDTO updateHabit(@Argument("id") Long id,
                                      @Argument("input") HabitInputDTO input) {
        return habitService.updateHabit(id, input);
    }

    @MutationMapping(name = "deleteHabit")
    public Boolean deleteHabit(@Argument("id") Long id) {
        habitService.deleteHabit(id);
        return true;
    }
}
