package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.Reminder.ReminderInputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderOutputDTO;
import una.ac.cr.FitFlow.model.Reminder;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.model.Habit;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class MapperForReminder {

    public ReminderOutputDTO toDto(Reminder r) {
        LocalDateTime dt = (r.getTime() == null)
                ? null
                : LocalDateTime.of(LocalDate.now(), r.getTime());
        return ReminderOutputDTO.builder()
                .id(r.getId())
                .userId(r.getUser() != null ? r.getUser().getId() : null)
                .habitId(r.getHabit() != null ? r.getHabit().getId() : null)
                .message(r.getMessage())
                .time(dt)
                .frequency(r.getFrequency() != null ? r.getFrequency().name() : null)
                .build();
    }

    public Reminder toEntity(ReminderInputDTO in, User user, Habit habit, Reminder.Frequency freq) {
        return Reminder.builder()
                .user(user)
                .habit(habit)
                .message(in.getMessage())
                .time(in.getTime().toLocalTime())
                .frequency(freq)
                .build();
    }

    public void copyToEntity(ReminderInputDTO in, Reminder target,
                             User userIfChanged, Habit habitIfChanged, Reminder.Frequency freqIfChanged) {
        if (userIfChanged != null)  target.setUser(userIfChanged);
        if (habitIfChanged != null) target.setHabit(habitIfChanged);
        if (in.getMessage() != null) target.setMessage(in.getMessage());
        if (in.getTime() != null)    target.setTime(in.getTime().toLocalTime());
        if (freqIfChanged != null)   target.setFrequency(freqIfChanged);
    }
}
