package una.ac.cr.FitFlow.service.Reminder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.Objects;

import una.ac.cr.FitFlow.dto.ReminderDTO;
import una.ac.cr.FitFlow.model.Reminder;
import una.ac.cr.FitFlow.model.Reminder.Frequency;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.repository.ReminderRepository;
import una.ac.cr.FitFlow.repository.UserRepository;
import una.ac.cr.FitFlow.repository.HabitRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReminderServiceImplementation implements ReminderService {
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;

    private Frequency parseFrequency(String freq) {
        if (freq == null) {
            throw new IllegalArgumentException("Frecuencia requerida. Use DAILY o WEEKLY.");
        }
        try {
            return Frequency.valueOf(freq.toUpperCase().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Frecuencia inválida. Use DAILY o WEEKLY.");
        }
    }

    private ReminderDTO convertToDto(Reminder reminder) {
        return ReminderDTO.builder()
                .id(reminder.getId())
                .userId(reminder.getUser() != null ? reminder.getUser().getId() : null)
                .habitId(reminder.getHabit() != null ? reminder.getHabit().getId() : null)
                .message(reminder.getMessage())
                .time(reminder.getTime() != null ? reminder.getTime().atDate(java.time.LocalDate.now()) : null)
                .frequency(reminder.getFrequency() != null ? reminder.getFrequency().name() : null)
                .build();
    }

    private Reminder toEntityForCreate(ReminderDTO d) {
        User user = userRepository.findById(d.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + d.getUserId()));
        Habit habit = habitRepository.findById(d.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + d.getHabitId()));

        if (d.getTime() == null) {
            throw new IllegalArgumentException("El campo 'time' es requerido");
        }

        return Reminder.builder()
                .user(user)
                .habit(habit)
                .message(d.getMessage())
                .time(d.getTime().toLocalTime())
                .frequency(parseFrequency(d.getFrequency()))
                .build();
    }

    private void applyForUpdate(Reminder target, ReminderDTO d) {
        if (d.getUserId() != null &&
                (target.getUser() == null || !Objects.equals(target.getUser().getId(), d.getUserId()))) {
            User user = userRepository.findById(d.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + d.getUserId()));
            target.setUser(user);
        }
        if (d.getHabitId() != null &&
                (target.getHabit() == null || !Objects.equals(target.getHabit().getId(), d.getHabitId()))) {
            Habit habit = habitRepository.findById(d.getHabitId())
                    .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + d.getHabitId()));
            target.setHabit(habit);
        }
        if (d.getMessage() != null) {
            target.setMessage(d.getMessage());
        }
        if (d.getTime() != null) {
            LocalTime lt = d.getTime().toLocalTime();
            target.setTime(lt);
        }
        if (d.getFrequency() != null) {
            target.setFrequency(parseFrequency(d.getFrequency()));
        }
    }

 
    @Override
    @Transactional
    public ReminderDTO create(ReminderDTO dto) {
        Reminder saved = reminderRepository.save(toEntityForCreate(dto));
        return convertToDto(saved);
    }

    @Override
    @Transactional
    public ReminderDTO update(Long id, ReminderDTO dto) {
        Reminder current = reminderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recordatorio no encontrado: " + id));
        applyForUpdate(current, dto);
        return convertToDto(reminderRepository.save(current));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!reminderRepository.existsById(id)) {
            throw new IllegalArgumentException("Recordatorio no encontrado: " + id);
        }
        reminderRepository.deleteById(id);
    }

    @Override
    public ReminderDTO findById(Long id) {
        return reminderRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new IllegalArgumentException("Recordatorio no encontrado: " + id));
    }

    @Override
    public Page<ReminderDTO> list(Pageable pageable) {
        return reminderRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    public Page<ReminderDTO> listByUserId(Long userId, Pageable pageable) {
        return reminderRepository.findByUserId(userId, pageable).map(this::convertToDto);
    }
}
