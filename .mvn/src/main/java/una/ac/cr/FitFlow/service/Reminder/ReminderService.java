package una.ac.cr.FitFlow.service.Reminder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.Reminder.ReminderInputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderOutputDTO;

public interface ReminderService {
    ReminderOutputDTO create(ReminderInputDTO dto);
    ReminderOutputDTO update(Long id, ReminderInputDTO dto);
    void delete(Long id);
    ReminderOutputDTO findById(Long id);
    Page<ReminderOutputDTO> list(Pageable pageable);
    Page<ReminderOutputDTO> listByUserId(Long userId, Pageable pageable);
}
