package una.ac.cr.FitFlow.service.Reminder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.ReminderDTO;

public interface ReminderService {
    ReminderDTO create(ReminderDTO dto);

    ReminderDTO update(Long id, ReminderDTO dto);

    void delete(Long id);

    ReminderDTO findById(Long id);

    Page<ReminderDTO> list(Pageable pageable);

    Page<ReminderDTO> listByUserId(Long userId, Pageable pageable);
}
