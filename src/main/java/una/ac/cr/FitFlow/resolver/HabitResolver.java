package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.HabitDTO;
import una.ac.cr.FitFlow.service.Habit.HabitService;

@Controller
@RequiredArgsConstructor
public class HabitResolver {
    private final HabitService habitService;

    @QueryMapping
    public HabitDTO getHabitById(@Argument Long id) {
        return habitService.findHabitById(id);
    }

    @QueryMapping
    public Page<HabitDTO> getHabitsDto(@Argument int page, @Argument int size, @Argument String keyword) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return habitService.listHabits(keyword, pageable);
    }

    @MutationMapping
    public HabitDTO createHabit(@Argument HabitDTO habit) {
        return habitService.createHabit(habit);
    }

    @MutationMapping
    public HabitDTO updateHabit(@Argument Long id, @Argument HabitDTO habit) {
        return habitService.updateHabit(id, habit);
    }

    @MutationMapping
    public void deleteHabit(@Argument Long id) {
        habitService.deleteHabit(id);
    }
}
