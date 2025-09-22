package una.ac.cr.FitFlow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import una.ac.cr.FitFlow.model.CompletedActivity;

import java.time.OffsetDateTime;
import java.util.List;

public interface CompletedActivityRepository extends JpaRepository<CompletedActivity, Long> {

    Page<CompletedActivity> findByNotesContainingIgnoreCase(String notes, Pageable pageable);

    Page<CompletedActivity> findByProgressLog_User_Id(Long userId, Pageable pageable);

    Page<CompletedActivity> findByProgressLog_Id(Long progressLogId, Pageable pageable);

    @Query("""
    SELECT ca
    FROM CompletedActivity ca
    JOIN ca.habit h
    WHERE ca.completedAt >= :start
      AND ca.completedAt < :end
      AND h.category = :category
    """)
List<CompletedActivity> findCompletedByCategoryAndMonth(
        @Param("category") una.ac.cr.FitFlow.model.Habit.Category category,
        @Param("start") OffsetDateTime start,
        @Param("end") OffsetDateTime end
);

List<CompletedActivity> findByHabit_Id(Long habitId);

}
