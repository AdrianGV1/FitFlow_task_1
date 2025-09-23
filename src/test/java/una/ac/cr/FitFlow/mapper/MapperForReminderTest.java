package una.ac.cr.FitFlow.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import una.ac.cr.FitFlow.dto.Reminder.ReminderInputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderOutputDTO;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.Reminder;
import una.ac.cr.FitFlow.model.User;

class MapperForReminderTest {

  private final MapperForReminder mapper = new MapperForReminder();

  private User user(Long id) {
    return User.builder().id(id).build();
  }

  private Habit habit(Long id) {
    return Habit.builder().id(id).build();
  }

  private OffsetDateTime odt(int y, int m, int d, int hh, int mm) {
    return OffsetDateTime.of(y, m, d, hh, mm, 0, 0, ZoneOffset.UTC);
  }

  /* ====================== toDto ====================== */

  @Test
  @DisplayName("toDto: mapea todos los campos, incluyendo userId/habitId/frequency/time")
  void toDto_ok() {
    OffsetDateTime nineThirty = odt(2025, 9, 22, 9, 30);

    Reminder r = Reminder.builder()
        .id(101L)
        .user(user(11L))
        .habit(habit(22L))
        .message("Beber agua")
        .time(nineThirty) // OffsetDateTime
        .frequency(Reminder.Frequency.DAILY)
        .build();

    ReminderOutputDTO dto = mapper.toDto(r);

    assertThat(dto.getId()).isEqualTo(101L);
    assertThat(dto.getUserId()).isEqualTo(11L);
    assertThat(dto.getHabitId()).isEqualTo(22L);
    assertThat(dto.getMessage()).isEqualTo("Beber agua");
    assertThat(dto.getTime()).isEqualTo(nineThirty);
    assertThat(dto.getFrequency()).isEqualTo("DAILY"); // name()
  }

  @Test
  @DisplayName("toDto: tolera user/habit/frequency/time null")
  void toDto_handlesNulls() {
    Reminder r = Reminder.builder()
        .id(1L)
        .user(null)                 // → userId null
        .habit(null)                // → habitId null
        .message(null)              // puede ser null
        .time(null)                 // OffsetDateTime null
        .frequency(null)            // → frequency null
        .build();

    ReminderOutputDTO dto = mapper.toDto(r);

    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getHabitId()).isNull();
    assertThat(dto.getFrequency()).isNull();
    assertThat(dto.getMessage()).isNull();
    assertThat(dto.getTime()).isNull();
  }

  /* ====================== toEntity ====================== */

  @Test
  @DisplayName("toEntity: construye Reminder con user/habit/frequency y message/time del input")
  void toEntity_ok() {
    OffsetDateTime twentyTwoFifteen = odt(2025, 9, 22, 22, 15);

    ReminderInputDTO in = ReminderInputDTO.builder()
        .message("Dormir temprano")
        .time(twentyTwoFifteen) // OffsetDateTime en el DTO
        .build();

    User u = user(7L);
    Habit h = habit(8L);

    Reminder r = mapper.toEntity(in, u, h, Reminder.Frequency.WEEKLY);

    assertThat(r.getUser()).isSameAs(u);
    assertThat(r.getHabit()).isSameAs(h);
    assertThat(r.getMessage()).isEqualTo("Dormir temprano");
    assertThat(r.getTime()).isEqualTo(twentyTwoFifteen);
    assertThat(r.getFrequency()).isEqualTo(Reminder.Frequency.WEEKLY);
  }

  /* ====================== copyToEntity ====================== */

  @Test
  @DisplayName("copyToEntity: cambia solo los campos provistos (user/habit/freq si vienen; message/time si input los trae)")
  void copyToEntity_changesSelectively() {
    OffsetDateTime originalTime = odt(2025, 9, 1, 8, 0);

    Reminder original = Reminder.builder()
        .user(user(1L))
        .habit(habit(2L))
        .message("Original")
        .time(originalTime)
        .frequency(Reminder.Frequency.DAILY)
        .build();

    OffsetDateTime newTime = odt(2025, 9, 2, 9, 45);

    ReminderInputDTO in = ReminderInputDTO.builder()
        .message("Nuevo mensaje")
        .time(newTime)
        .build();

    User newUser = user(100L);
    Habit newHabit = habit(200L);

    // Cambiamos de DAILY -> WEEKLY (no existe MONTHLY en tu enum)
    mapper.copyToEntity(in, original, newUser, newHabit, Reminder.Frequency.WEEKLY);

    assertThat(original.getUser().getId()).isEqualTo(100L);
    assertThat(original.getHabit().getId()).isEqualTo(200L);
    assertThat(original.getMessage()).isEqualTo("Nuevo mensaje");
    assertThat(original.getTime()).isEqualTo(newTime);
    assertThat(original.getFrequency()).isEqualTo(Reminder.Frequency.WEEKLY);
  }

  @Test
  @DisplayName("copyToEntity: no cambia campos cuando IfChanged y el input son null")
  void copyToEntity_noChangeWhenNulls() {
    OffsetDateTime sevenThirty = odt(2025, 9, 1, 7, 30);

    Reminder original = Reminder.builder()
        .user(user(10L))
        .habit(habit(20L))
        .message("Mantener")
        .time(sevenThirty)
        .frequency(Reminder.Frequency.WEEKLY)
        .build();

    ReminderInputDTO in = ReminderInputDTO.builder()
        .message(null)
        .time(null) // OffsetDateTime null
        .build();

    mapper.copyToEntity(in, original, null, null, null);

    assertThat(original.getUser().getId()).isEqualTo(10L);
    assertThat(original.getHabit().getId()).isEqualTo(20L);
    assertThat(original.getMessage()).isEqualTo("Mantener");
    assertThat(original.getTime()).isEqualTo(sevenThirty);
    assertThat(original.getFrequency()).isEqualTo(Reminder.Frequency.WEEKLY);
  }
}