package una.ac.cr.FitFlow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import una.ac.cr.FitFlow.model.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    Page<Reminder> findByUser_Id(Long userId, Pageable pageable);
}
