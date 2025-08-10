package una.ac.cr.FitFlow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import una.ac.cr.FitFlow.model.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserId(Long userId);
}