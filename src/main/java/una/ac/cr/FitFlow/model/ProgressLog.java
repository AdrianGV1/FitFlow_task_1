package una.ac.cr.FitFlow.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "progress_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "routine_id", nullable = false)
  private Routine routine;

  @Column(name = "log_date", nullable = false)
  private OffsetDateTime logDate;   

  @OneToMany(mappedBy = "progressLog", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CompletedActivity> completedActivities;
}
