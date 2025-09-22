package una.ac.cr.FitFlow.dto.ProgressLog;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProgressLogInputDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_passesValidation() {
        ProgressLogInputDTO dto = ProgressLogInputDTO.builder()
                .id(1L)
                .userId(10L)
                .routineId(20L)
                .date(OffsetDateTime.now())
                .completedActivityIds(List.of(100L, 200L))
                .build();

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullUserId_failsValidation() {
        ProgressLogInputDTO dto = ProgressLogInputDTO.builder()
                .routineId(20L)
                .date(OffsetDateTime.now())
                .build();

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("userId"));
    }

    @Test
    void negativeRoutineId_failsValidation() {
        ProgressLogInputDTO dto = ProgressLogInputDTO.builder()
                .userId(10L)
                .routineId(-5L) // inválido
                .date(OffsetDateTime.now())
                .build();

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("routineId"));
    }

    @Test
    void futureDate_failsValidation() {
        ProgressLogInputDTO dto = ProgressLogInputDTO.builder()
                .userId(10L)
                .routineId(20L)
                .date(OffsetDateTime.now().plusDays(1)) // futuro
                .build();

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("date"));
    }

    @Test
    void negativeCompletedActivityId_failsValidation() {
        ProgressLogInputDTO dto = ProgressLogInputDTO.builder()
                .userId(10L)
                .routineId(20L)
                .date(OffsetDateTime.now())
                .completedActivityIds(List.of(-1L)) // válido crear la lista, pero inválido por @Positive
                .build();

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("completedActivityIds"));
    }

    @Test
    void nullCompletedActivityId_failsValidation() {
        ProgressLogInputDTO dto = ProgressLogInputDTO.builder()
                .userId(10L)
                .routineId(20L)
                .date(OffsetDateTime.now())
                .completedActivityIds(Arrays.asList(100L, null)) // Arrays.asList sí permite null
                .build();

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("completedActivityIds"));
    }
}
