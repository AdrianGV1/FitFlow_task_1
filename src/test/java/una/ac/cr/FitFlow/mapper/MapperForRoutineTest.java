package una.ac.cr.FitFlow.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import una.ac.cr.FitFlow.dto.Routine.RoutineInputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.RoutineActivity;
import una.ac.cr.FitFlow.model.User;

class MapperForRoutineTest {

  private final MapperForRoutine mapper = new MapperForRoutine();

  // Helpers mínimos
  private User user(Long id) {
    return User.builder().id(id).build();
  }

  private RoutineActivity ra(Long id) {
    // Para el mapper solo importa el ID en este test unitario
    return RoutineActivity.builder()
        .id(id)
        .duration(30)
        .notes(null)
        .build();
  }

  /* ====================== toDto ====================== */

  @Test
  @DisplayName("toDto: mapea id, title, userId, daysOfWeek y activityIds (filtra IDs nulos)")
  void toDto_ok() {
    Routine r = Routine.builder()
        .id(77L)
        .title("Mañanas activas")
        .user(user(9L))
        .daysOfWeek(List.of(Routine.DaysOfWeek.MON, Routine.DaysOfWeek.WED))
        .activities(List.of(ra(1L), ra(null), ra(2L))) // filtra null
        .build();

    RoutineOutputDTO dto = mapper.toDto(r);

    assertThat(dto.getId()).isEqualTo(77L);
    assertThat(dto.getTitle()).isEqualTo("Mañanas activas");
    assertThat(dto.getUserId()).isEqualTo(9L);
    assertThat(dto.getDaysOfWeek())
        .containsExactlyInAnyOrder(Routine.DaysOfWeek.MON, Routine.DaysOfWeek.WED);
    assertThat(dto.getActivityIds()).containsExactlyInAnyOrder(1L, 2L);
  }

  @Test
  @DisplayName("toDto: tolera user null, activities null y daysOfWeek null")
  void toDto_handlesNulls() {
    Routine r = Routine.builder()
        .id(1L)
        .title("Rutina sin user ni activities")
        .user(null)           // → userId null
        .daysOfWeek(null)     // permitido: pasa tal cual
        .activities(null)     // → lista vacía en DTO
        .build();

    RoutineOutputDTO dto = mapper.toDto(r);

    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getDaysOfWeek()).isNull();   // el mapper pasa tal cual
    assertThat(dto.getActivityIds()).isEmpty(); // mapper convierte null → []
  }

  /* ====================== toEntity ====================== */

  @Test
  @DisplayName("toEntity: construye Routine con title, user y daysOfWeek del input")
  void toEntity_ok() {
    RoutineInputDTO in = RoutineInputDTO.builder()
        .title("Noche ligera")
        .daysOfWeek(List.of(Routine.DaysOfWeek.FRI))
        .build();

    User u = user(100L);

    Routine r = mapper.toEntity(in, u);

    assertThat(r.getTitle()).isEqualTo("Noche ligera");
    assertThat(r.getUser()).isSameAs(u);
    assertThat(r.getDaysOfWeek()).containsExactly(Routine.DaysOfWeek.FRI);
  }

  /* ====================== copyToEntity ====================== */

  @Test
  @DisplayName("copyToEntity: cambia solo los campos provistos (title/daysOfWeek si vienen; user si cambia)")
  void copyToEntity_changesSelectively() {
    Routine original = Routine.builder()
        .title("Original")
        .user(user(1L))
        .daysOfWeek(List.of(Routine.DaysOfWeek.MON))
        .build();

    RoutineInputDTO in = RoutineInputDTO.builder()
        .title("Nuevo título")
        .daysOfWeek(List.of(Routine.DaysOfWeek.TUE, Routine.DaysOfWeek.THU))
        .build();

    User newUser = user(200L);

    mapper.copyToEntity(in, original, newUser);

    assertThat(original.getTitle()).isEqualTo("Nuevo título");
    assertThat(original.getUser().getId()).isEqualTo(200L);
    assertThat(original.getDaysOfWeek())
        .containsExactlyInAnyOrder(Routine.DaysOfWeek.TUE, Routine.DaysOfWeek.THU); // <-- corregido
  }

  @Test
  @DisplayName("copyToEntity: no cambia campos cuando input y userIfChanged son null")
  void copyToEntity_noChangeWhenNulls() {
    Routine original = Routine.builder()
        .title("Se mantiene")
        .user(user(9L))
        .daysOfWeek(List.of(Routine.DaysOfWeek.WED))
        .build();

    RoutineInputDTO in = RoutineInputDTO.builder()
        .title(null)
        .daysOfWeek(null)
        .build();

    mapper.copyToEntity(in, original, null);

    assertThat(original.getTitle()).isEqualTo("Se mantiene");
    assertThat(original.getUser().getId()).isEqualTo(9L);
    assertThat(original.getDaysOfWeek()).containsExactly(Routine.DaysOfWeek.WED);
  }
}