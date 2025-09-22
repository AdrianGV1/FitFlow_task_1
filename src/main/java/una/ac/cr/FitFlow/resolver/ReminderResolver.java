package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderInputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderOutputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderPageDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.Reminder.ReminderService;
import una.ac.cr.FitFlow.service.user.UserService;

@Controller
@RequiredArgsConstructor
public class ReminderResolver {

    private static final Role.Module MODULE = Role.Module.RECORDATORIOS;

    private final ReminderService reminderService;
    private final UserService userService;
    private final HabitService habitService;

    @QueryMapping(name = "reminderById")
    public ReminderOutputDTO reminderById(@Argument("id") Long id) {
        SecurityUtils.requireRead(MODULE);
        return reminderService.findById(id);
    }

    @QueryMapping(name = "reminders")
    public ReminderPageDTO reminders(@Argument("page") int page, @Argument("size") int size) {
        SecurityUtils.requireRead(MODULE);
        Pageable pageable = PageRequest.of(page, size);
        Page<ReminderOutputDTO> p = reminderService.list(pageable);
        return ReminderPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @QueryMapping(name = "remindersByUserId")
    public ReminderPageDTO remindersByUserId(@Argument("userId") Long userId,
                                             @Argument("page") int page,
                                             @Argument("size") int size) {
        SecurityUtils.requireRead(MODULE);
        Pageable pageable = PageRequest.of(page, size);
        Page<ReminderOutputDTO> p = reminderService.listByUserId(userId, pageable);
        return ReminderPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @MutationMapping(name = "createReminder")
    public ReminderOutputDTO createReminder(@Argument("input") ReminderInputDTO input) {
        SecurityUtils.requireWrite(MODULE);
        return reminderService.create(input);
    }

    @MutationMapping(name = "updateReminder")
    public ReminderOutputDTO updateReminder(@Argument("id") Long id,
                                            @Argument("input") ReminderInputDTO input) {
        SecurityUtils.requireWrite(MODULE);
        return reminderService.update(id, input);
    }

    @MutationMapping(name = "deleteReminder")
    public Boolean deleteReminder(@Argument("id") Long id) {
        SecurityUtils.requireWrite(MODULE);
        reminderService.delete(id);
        return true;
    }

    @SchemaMapping(typeName = "Reminder", field = "user")
    public UserOutputDTO user(ReminderOutputDTO r) {
        return userService.findUserById(r.getUserId());
    }

    @SchemaMapping(typeName = "Reminder", field = "habit")
    public HabitOutputDTO habit(ReminderOutputDTO r) {
        return r.getHabitId() == null ? null : habitService.findHabitById(r.getHabitId());
    }
}
