package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.ReminderDTO;
import una.ac.cr.FitFlow.dto.ReminderPageDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.service.Reminder.ReminderService;
import una.ac.cr.FitFlow.dto.HabitDTO;
import una.ac.cr.FitFlow.service.user.UserService;
import una.ac.cr.FitFlow.service.Habit.HabitService;

@Controller
@RequiredArgsConstructor
public class ReminderResolver {
    private final ReminderService reminderService;
    private final UserService userService;
    private final HabitService habitService;

    @QueryMapping(name = "reminderById")
    public ReminderDTO reminderById(@Argument("id") Long id) {
        return reminderService.findById(id);
    }

    @QueryMapping(name = "reminders")
    public ReminderPageDTO reminders(@Argument("page") int page, @Argument("size") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<ReminderDTO> pageResult = reminderService.list(pageable);
        return ReminderPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @QueryMapping(name = "remindersByUserId")
    public ReminderPageDTO remindersByUserId(@Argument("userId") Long userId, @Argument("page") int page,
            @Argument("size") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<ReminderDTO> pageResult = reminderService.listByUserId(userId, pageable);
        return ReminderPageDTO.builder()
                .content(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }

    @MutationMapping(name = "createReminder")
    public ReminderDTO createReminder(@Argument("input") ReminderDTO reminderDTO) {
        return reminderService.create(reminderDTO);
    }

    @MutationMapping(name = "updateReminder")
    public ReminderDTO updateReminder(@Argument("id") Long id, @Argument("input") ReminderDTO reminderDTO) {
        return reminderService.update(id, reminderDTO);
    }

    @MutationMapping(name = "deleteReminder")
    public Boolean deleteReminder(@Argument("id") Long id) {
        reminderService.delete(id);
        return true;
    }

    @SchemaMapping(typeName = "Reminder")
    public UserOutputDTO user(ReminderDTO reminder) {
        return userService.findUserById(reminder.getUserId());
    }

    @SchemaMapping(typeName = "Reminder")
    public HabitDTO habit(ReminderDTO reminder) {
        if (reminder.getHabitId() == null) {
            return null;
        }
        return habitService.findHabitById(reminder.getHabitId());
    }
}

