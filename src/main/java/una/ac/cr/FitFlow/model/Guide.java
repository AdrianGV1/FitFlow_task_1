package una.ac.cr.FitFlow.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "guides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Guide {

    public enum Category {
        PHYSICAL, MENTAL, SLEEP, DIET
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "guide_recommended_habit", joinColumns = @JoinColumn(name = "guide_id"), inverseJoinColumns = @JoinColumn(name = "habit_id"))

    @Builder.Default
    private Set<Habit> recommendedHabits = new HashSet<>();
}
