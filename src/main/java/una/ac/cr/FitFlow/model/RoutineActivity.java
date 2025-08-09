package una.ac.cr.FitFlow.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "routine_activities", uniqueConstraints = {
        @UniqueConstraint(name = "uk_routine_habit", columnNames = { "routine_id", "habit_id" })
})
public class RoutineActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(nullable = false)
    private Integer duration;

    @Column(columnDefinition = "TEXT", length = 100)
    private String notes;
}
