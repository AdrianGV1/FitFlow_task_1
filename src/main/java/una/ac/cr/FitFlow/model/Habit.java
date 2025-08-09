package una.ac.cr.FitFlow.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "habits")
@ToString(onlyExplicitlyIncluded = true)
public class Habit {

    public enum Category {
        PHYSICAL, MENTAL, SLEEP, DIET
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "favoriteHabits", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoutineActivity> routineActivities = new ArrayList<>();

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompletedActivity> completedActivities = new ArrayList<>();

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();

    @ManyToMany(mappedBy = "recommendedHabits", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Guide> guides = new HashSet<>();
}
