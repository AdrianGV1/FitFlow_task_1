package una.ac.cr.FitFlow.resolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.ReminderDTO;
import una.ac.cr.FitFlow.service.Reminder.ReminderService;

@Controller
@RequiredArgsConstructor
public class ReminderResolver {
    private final ReminderService reminderService;

    @QueryMapping
    public ReminderDTO getReminderById(@Argument Long id) {
        return reminderService.findById(id);
    }

    @QueryMapping
    public Page<ReminderDTO> getAllReminders(@Argument int page, @Argument int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return reminderService.list(pageable);
    }

    @QueryMapping
    public Page<ReminderDTO> getRemindersByUserId(@Argument Long userId, @Argument int page, @Argument int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return reminderService.listByUserId(userId, pageable);
    }

    @MutationMapping
    public ReminderDTO createReminder(@Argument ReminderDTO reminderDTO) {
        return reminderService.create(reminderDTO);
    }

    @MutationMapping
    public ReminderDTO updateReminder(@Argument Long id, @Argument ReminderDTO reminderDTO) {
        return reminderService.update(id, reminderDTO);
    }

    @MutationMapping
    public void deleteReminder(@Argument Long id) {
        reminderService.delete(id);
    }
}
