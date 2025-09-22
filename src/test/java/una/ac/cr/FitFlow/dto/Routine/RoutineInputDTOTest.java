package una.ac.cr.FitFlow.dto.Routine;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import una.ac.cr.FitFlow.model.Routine.DaysOfWeek;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoutineInputDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_passesValidation() {
        RoutineInputDTO dto = RoutineInputDTO.builder()
                .id(1L)
                .title("Morning routine")
                .userId(10L)
                .daysOfWeek(List.of(DaysOfWeek.MON, DaysOfWeek.WED))
                .activityIds(List.of(100L, 200L))
                .build();

        Set<ConstraintViolation<RoutineInputDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void tooLongTitle_failsValidation() {
        String longTitle = "x".repeat(151);
        RoutineInputDTO dto = RoutineInputDTO.builder()
                .title(longTitle)
                .userId(5L)
                .daysOfWeek(List.of(DaysOfWeek.FRI))
                .build();

        Set<ConstraintViolation<RoutineInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    void negativeUserId_failsValidation() {
        RoutineInputDTO dto = RoutineInputDTO.builder()
                .title("Evening routine")
                .userId(-1L)
                .daysOfWeek(List.of(DaysOfWeek.SAT))
                .build();

        Set<ConstraintViolation<RoutineInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("userId"));
    }

    @Test
    void nullDaysOfWeek_failsValidation() {
        RoutineInputDTO dto = RoutineInputDTO.builder()
                .title("Yoga routine")
                .userId(1L)
                .daysOfWeek(null) // inválido
                .build();

        Set<ConstraintViolation<RoutineInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("daysOfWeek"));
    }

    @Test
    void invalidActivityIds_failsValidation() {
        RoutineInputDTO dto = RoutineInputDTO.builder()
                .title("Workout routine")
                .userId(2L)
                .daysOfWeek(List.of(DaysOfWeek.MON))
                .activityIds(Arrays.asList(1L, null, -5L)) // inválidos
                .build();

        Set<ConstraintViolation<RoutineInputDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("activityIds"));
    }
}
