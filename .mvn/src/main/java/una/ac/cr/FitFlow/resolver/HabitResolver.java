package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.HabitDTO;
import una.ac.cr.FitFlow.dto.HabitPageDTO;
import una.ac.cr.FitFlow.service.Habit.HabitService;

@Controller
@RequiredArgsConstructor
public class HabitResolver {
    private final HabitService habitService;

    @QueryMapping(name = "habitById")
    public HabitDTO habitById(@Argument("id") Long id) {
        return habitService.findHabitById(id);
    }

    @QueryMapping(name = "habits")
    public HabitPageDTO habits(@Argument("page") int page, @Argument("size") int size,
            @Argument("keyword") String keyword) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<HabitDTO> pageResult = habitService.listHabits(keyword, pageable);
        return HabitPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @MutationMapping(name = "createHabit")
    public HabitDTO createHabit(@Argument("input") HabitDTO habit) {
        return habitService.createHabit(habit);
    }

    @MutationMapping(name = "updateHabit")
    public HabitDTO updateHabit(@Argument("id") Long id, @Argument("input") HabitDTO habit) {
        return habitService.updateHabit(id, habit);
    }

    @MutationMapping(name = "deleteHabit")
    public Boolean deleteHabit(@Argument("id") Long id) {
        habitService.deleteHabit(id);
        return true;
    }
}
