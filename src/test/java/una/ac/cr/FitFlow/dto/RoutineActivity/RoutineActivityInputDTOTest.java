package una.ac.cr.FitFlow.dto.RoutineActivity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoutineActivityInputDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_passesValidation() {
        RoutineActivityInputDTO dto = RoutineActivityInputDTO.builder()
                .id(1L)
                .routineId(10L)
                .habitId(20L)
                .duration(30)
                .notes("Evening yoga session")
                .build();

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullRoutineId_failsValidation() {
        RoutineActivityInputDTO dto = RoutineActivityInputDTO.builder()
                .habitId(5L)
                .duration(10)
                .build();

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("routineId"));
    }

    @Test
    void negativeHabitId_failsValidation() {
        RoutineActivityInputDTO dto = RoutineActivityInputDTO.builder()
                .routineId(1L)
                .habitId(-2L)
                .duration(15)
                .build();

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("habitId"));
    }

    @Test
    void durationTooSmall_failsValidation() {
        RoutineActivityInputDTO dto = RoutineActivityInputDTO.builder()
                .routineId(1L)
                .habitId(2L)
                .duration(0) // inválido, debe ser >= 1
                .build();

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("duration"));
    }

    @Test
    void notesTooLong_failsValidation() {
        String longNotes = "x".repeat(501);
        RoutineActivityInputDTO dto = RoutineActivityInputDTO.builder()
                .routineId(1L)
                .habitId(2L)
                .duration(15)
                .notes(longNotes) // inválido
                .build();

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("notes"));
    }
}
