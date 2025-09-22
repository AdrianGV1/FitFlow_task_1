package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import una.ac.cr.FitFlow.dto.Habit.HabitInputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitPageDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderOutputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.user.UserService;
import una.ac.cr.FitFlow.service.RoutineActivity.RoutineActivityService;
import una.ac.cr.FitFlow.service.CompletedActivity.CompletedActivityService;
import una.ac.cr.FitFlow.service.Reminder.ReminderService;
import una.ac.cr.FitFlow.service.Guide.GuideService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HabitResolver {

    private static final Role.Module MODULE = Role.Module.ACTIVIDADES;

    private final HabitService habitService;
    private final UserService userService;
    private final RoutineActivityService routineActivityService;
    private final CompletedActivityService completedActivityService;
    private final ReminderService reminderService;
    private final GuideService guideService;

    /* ================== Queries ================== */

    @QueryMapping(name = "habitById")
    public HabitOutputDTO habitById(@Argument("id") Long id) {
        SecurityUtils.requireRead(MODULE);
        return habitService.findHabitById(id);
    }

    @QueryMapping(name = "habits")
    public HabitPageDTO habits(@Argument("page") int page,
                               @Argument("size") int size,
                               @Argument("keyword") String keyword) {
        SecurityUtils.requireRead(MODULE);
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

    /* ================== Mutations ================== */

    @MutationMapping(name = "createHabit")
    public HabitOutputDTO createHabit(@Argument("input") HabitInputDTO input) {
        SecurityUtils.requireWrite(MODULE);
        return habitService.createHabit(input);
    }

    @MutationMapping(name = "updateHabit")
    public HabitOutputDTO updateHabit(@Argument("id") Long id,
                                      @Argument("input") HabitInputDTO input) {
        SecurityUtils.requireWrite(MODULE);
        return habitService.updateHabit(id, input);
    }

    @MutationMapping(name = "deleteHabit")
    public Boolean deleteHabit(@Argument("id") Long id) {
        SecurityUtils.requireWrite(MODULE);
        habitService.deleteHabit(id);
        return true;
    }

    /* ================== Relational Fields ================== */

    @SchemaMapping(typeName = "Habit", field = "users")
    public List<UserOutputDTO> users(HabitOutputDTO habit) {
        return userService.findUsersByHabitId(habit.getId());
    }

    @SchemaMapping(typeName = "Habit", field = "routineActivities")
    public List<RoutineActivityOutputDTO> routineActivities(HabitOutputDTO habit) {
        return routineActivityService.listByHabitId(habit.getId(), Pageable.unpaged()).getContent();
    }

    @SchemaMapping(typeName = "Habit", field = "completedActivities")
    public List<CompletedActivityOutputDTO> completedActivities(HabitOutputDTO habit) {
        // necesitas agregar este método al servicio si aún no lo tienes
        return completedActivityService.findByHabitId(habit.getId());
    }

    @SchemaMapping(typeName = "Habit", field = "reminders")
    public List<ReminderOutputDTO> reminders(HabitOutputDTO habit) {
        // igual: agregar un método listByHabitId en ReminderService
        return reminderService.listByHabitId(habit.getId(), Pageable.unpaged()).getContent();
    }

    @SchemaMapping(typeName = "Habit", field = "guides")
    public List<GuideOutputDTO> guides(HabitOutputDTO habit) {
        // necesitarías un método en GuideService → findByHabitId
        return guideService.findByHabitId(habit.getId());
    }

    
}
