package una.ac.cr.FitFlow.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.model.CompletedActivity;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.User;

class MapperForProgressLogTest {

  private final MapperForProgressLog mapper = new MapperForProgressLog();

  private User user(Long id) {
    return User.builder().id(id).build();
  }

  private Routine routine(Long id) {
    return Routine.builder().id(id).build();
  }

  private CompletedActivity ca(Long id) {
    return CompletedActivity.builder().id(id).build();
  }

  /* ====================== toDto ====================== */

  @Test
  @DisplayName("toDto: mapea campos básicos y completedActivityIds")
  void toDto_ok_withActivities() {
    var pl = ProgressLog.builder()
        .id(123L)
        .user(user(10L))
        .routine(routine(20L))
        .logDate(OffsetDateTime.parse("2025-09-01T12:00:00Z"))
        .completedActivities(Arrays.asList(ca(1L), ca(2L), ca(3L)))
        .build();

    ProgressLogOutputDTO dto = mapper.toDto(pl);

    assertThat(dto.getId()).isEqualTo(123L);
    assertThat(dto.getUserId()).isEqualTo(10L);
    assertThat(dto.getRoutineId()).isEqualTo(20L);
    assertThat(dto.getDate()).isEqualTo(OffsetDateTime.parse("2025-09-01T12:00:00Z"));
    assertThat(dto.getCompletedActivityIds()).containsExactly(1L, 2L, 3L);
  }

  @Test
  @DisplayName("toDto: tolera completedActivities == null y IDs nulos")
  void toDto_handlesNulls() {
    var pl = ProgressLog.builder()
        .id(1L)
        .user(null)         // user null → userId null
        .routine(null)      // routine null → routineId null
        .logDate(OffsetDateTime.parse("2025-08-31T00:00:00Z"))
        .completedActivities(null) // lista null → lista vacía
        .build();

    var dto = mapper.toDto(pl);
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getRoutineId()).isNull();
    assertThat(dto.getCompletedActivityIds()).isEmpty();

    // Ahora lista con un ID nulo: debe filtrarlo
    pl.setCompletedActivities(Arrays.asList(ca(11L), ca(null), ca(12L)));
    dto = mapper.toDto(pl);
    assertThat(dto.getCompletedActivityIds()).containsExactly(11L, 12L);
  }

  /* ====================== toEntity ====================== */

  @Test
  @DisplayName("toEntity: construye ProgressLog con user, routine y date del input")
  void toEntity_ok() {
    var when = OffsetDateTime.parse("2025-09-10T06:30:00Z");
    var in = ProgressLogInputDTO.builder().date(when).build();

    var u = user(77L);
    var r = routine(88L);

    ProgressLog pl = mapper.toEntity(in, u, r);

    assertThat(pl.getUser()).isSameAs(u);
    assertThat(pl.getRoutine()).isSameAs(r);
    assertThat(pl.getLogDate()).isEqualTo(when);
  }

  /* ====================== copyToEntity ====================== */

  @Test
  @DisplayName("copyToEntity: cambia solo los campos provistos (user/routine no nulos; date si viene)")
  void copyToEntity_changesSelectively() {
    var originalDate = OffsetDateTime.parse("2025-09-01T00:00:00Z");
    var target = ProgressLog.builder()
        .user(user(1L))
        .routine(routine(2L))
        .logDate(originalDate)
        .build();

    // Caso 1: cambiar user y routine; mantener date (input sin date)
    var inNoDate = ProgressLogInputDTO.builder().date(null).build();
    var newUser = user(100L);
    var newRoutine = routine(200L);

    mapper.copyToEntity(inNoDate, target, newUser, newRoutine);

    assertThat(target.getUser().getId()).isEqualTo(100L);
    assertThat(target.getRoutine().getId()).isEqualTo(200L);
    assertThat(target.getLogDate()).isEqualTo(originalDate); // no cambió

    // Caso 2: solo cambiar date si viene en el input; user/routine null → no tocar
    var newDate = OffsetDateTime.parse("2025-09-02T12:34:56Z");
    var inWithDate = ProgressLogInputDTO.builder().date(newDate).build();

    mapper.copyToEntity(inWithDate, target, null, null);

    assertThat(target.getUser().getId()).isEqualTo(100L);      // igual
    assertThat(target.getRoutine().getId()).isEqualTo(200L);   // igual
    assertThat(target.getLogDate()).isEqualTo(newDate);        // actualizado
  }
}