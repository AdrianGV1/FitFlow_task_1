package una.ac.cr.FitFlow.service.Reminder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.Reminder.ReminderInputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForReminder;
import una.ac.cr.FitFlow.model.Reminder;
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
    private final MapperForReminder mapper;

    private Reminder.Frequency parseFrequency(String raw) {
        if (raw == null) return null;
        try {
            return Reminder.Frequency.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Frecuencia inválida. Use DAILY o WEEKLY.");
        }
    }

    private ReminderOutputDTO toDto(Reminder r) { return mapper.toDto(r); }

    @Override
    @Transactional
    public ReminderOutputDTO create(ReminderInputDTO dto) {
        if (dto.getUserId() == null)  throw new IllegalArgumentException("userId es obligatorio.");
        if (dto.getHabitId() == null) throw new IllegalArgumentException("habitId es obligatorio.");
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty())
            throw new IllegalArgumentException("message es obligatorio.");
        if (dto.getTime() == null)    throw new IllegalArgumentException("time es obligatorio.");
        if (dto.getFrequency() == null || dto.getFrequency().trim().isEmpty())
            throw new IllegalArgumentException("frequency es obligatorio.");

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));
        Habit habit = habitRepository.findById(dto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));
        Reminder.Frequency freq = parseFrequency(dto.getFrequency());

        Reminder saved = reminderRepository.save(mapper.toEntity(dto, user, habit, freq));
        return toDto(saved);
    }

    @Override
    @Transactional
    public ReminderOutputDTO update(Long id, ReminderInputDTO dto) {
        Reminder current = reminderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recordatorio no encontrado: " + id));

        User userIfChanged = null;
        if (dto.getUserId() != null &&
                (current.getUser() == null || !dto.getUserId().equals(current.getUser().getId()))) {
            userIfChanged = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));
        }

        Habit habitIfChanged = null;
        if (dto.getHabitId() != null &&
                (current.getHabit() == null || !dto.getHabitId().equals(current.getHabit().getId()))) {
            habitIfChanged = habitRepository.findById(dto.getHabitId())
                    .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));
        }

        Reminder.Frequency freqIfChanged = (dto.getFrequency() != null)
                ? parseFrequency(dto.getFrequency())
                : null;

        mapper.copyToEntity(dto, current, userIfChanged, habitIfChanged, freqIfChanged);

        Reminder saved = reminderRepository.save(current);
        return toDto(saved);
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
    public ReminderOutputDTO findById(Long id) {
        return reminderRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Recordatorio no encontrado: " + id));
    }

    @Override
    public Page<ReminderOutputDTO> list(Pageable pageable) {
        return reminderRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<ReminderOutputDTO> listByUserId(Long userId, Pageable pageable) {
        return reminderRepository.findByUser_Id(userId, pageable).map(this::toDto);
    }
}
