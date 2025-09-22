package una.ac.cr.FitFlow.dto.Reminder;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderInputDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_passesValidation() {
        ReminderInputDTO dto = ReminderInputDTO.builder()
                .id(1L)
                .userId(10L)
                .habitId(20L)
                .message("Drink a glass of water")
                .time(OffsetDateTime.now())
                .frequency("DAILY")
                .build();

        Set<ConstraintViolation<ReminderInputDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullUserId_failsValidation() {
        ReminderInputDTO dto = ReminderInputDTO.builder()
                .habitId(20L)
                .message("Test")
                .time(OffsetDateTime.now())
                .frequency("DAILY")
                .build();

        Set<ConstraintViolation<ReminderInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("userId"));
    }

    @Test
    void negativeHabitId_failsValidation() {
        ReminderInputDTO dto = ReminderInputDTO.builder()
                .userId(10L)
                .habitId(-5L)
                .message("Test")
                .time(OffsetDateTime.now())
                .frequency("DAILY")
                .build();

        Set<ConstraintViolation<ReminderInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("habitId"));
    }

    @Test
    void blankMessage_failsValidation() {
        ReminderInputDTO dto = ReminderInputDTO.builder()
                .userId(10L)
                .habitId(20L)
                .message("   ")
                .time(OffsetDateTime.now())
                .frequency("DAILY")
                .build();

        Set<ConstraintViolation<ReminderInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("message"));
    }

    @Test
    void tooLongMessage_failsValidation() {
        String longMsg = "x".repeat(256);
        ReminderInputDTO dto = ReminderInputDTO.builder()
                .userId(10L)
                .habitId(20L)
                .message(longMsg)
                .time(OffsetDateTime.now())
                .frequency("DAILY")
                .build();

        Set<ConstraintViolation<ReminderInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("message"));
    }

    @Test
    void nullTime_failsValidation() {
        ReminderInputDTO dto = ReminderInputDTO.builder()
                .userId(10L)
                .habitId(20L)
                .message("Reminder")
                .frequency("DAILY")
                .build();

        Set<ConstraintViolation<ReminderInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("time"));
    }

    @Test
    void blankFrequency_failsValidation() {
        ReminderInputDTO dto = ReminderInputDTO.builder()
                .userId(10L)
                .habitId(20L)
                .message("Reminder")
                .time(OffsetDateTime.now())
                .frequency("   ")
                .build();

        Set<ConstraintViolation<ReminderInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("frequency"));
    }

    @Test
    void tooLongFrequency_failsValidation() {
        String longFrequency = "a".repeat(21);
        ReminderInputDTO dto = ReminderInputDTO.builder()
                .userId(10L)
                .habitId(20L)
                .message("Reminder")
                .time(OffsetDateTime.now())
                .frequency(longFrequency)
                .build();

        Set<ConstraintViolation<ReminderInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("frequency"));
    }
}
