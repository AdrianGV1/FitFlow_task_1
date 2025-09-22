package una.ac.cr.FitFlow.dto.CompletedActivity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CompletedActivityInputDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_passesValidation() {
        CompletedActivityInputDTO dto = CompletedActivityInputDTO.builder()
                .id(1L)
                .completedAt(OffsetDateTime.now())
                .notes("Some notes")
                .progressLogId(10L)
                .habitId(20L)
                .build();

        Set<ConstraintViolation<CompletedActivityInputDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullCompletedAt_failsValidation() {
        CompletedActivityInputDTO dto = CompletedActivityInputDTO.builder()
                .progressLogId(10L)
                .habitId(20L)
                .build();

        Set<ConstraintViolation<CompletedActivityInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("completedAt"));
    }

    @Test
    void notesTooLong_failsValidation() {
        String longText = "a".repeat(600);

        CompletedActivityInputDTO dto = CompletedActivityInputDTO.builder()
                .completedAt(OffsetDateTime.now())
                .notes(longText)
                .progressLogId(10L)
                .habitId(20L)
                .build();

        Set<ConstraintViolation<CompletedActivityInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("notes"));
    }

    @Test
    void negativeProgressLogId_failsValidation() {
        CompletedActivityInputDTO dto = CompletedActivityInputDTO.builder()
                .completedAt(OffsetDateTime.now())
                .progressLogId(-1L)
                .habitId(5L)
                .build();

        Set<ConstraintViolation<CompletedActivityInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("progressLogId"));
    }

    @Test
    void nullHabitId_failsValidation() {
        CompletedActivityInputDTO dto = CompletedActivityInputDTO.builder()
                .completedAt(OffsetDateTime.now())
                .progressLogId(5L)
                .habitId(null)
                .build();

        Set<ConstraintViolation<CompletedActivityInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("habitId"));
    }
}
