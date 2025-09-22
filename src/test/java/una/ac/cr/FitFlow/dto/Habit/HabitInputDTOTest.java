package una.ac.cr.FitFlow.dto.Habit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class HabitInputDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_passesValidation() {
        HabitInputDTO dto = HabitInputDTO.builder()
                .id(1L)
                .name("Morning Run")
                .category("PHYSICAL")
                .description("Go for a 5km run in the morning.")
                .build();

        Set<ConstraintViolation<HabitInputDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_failsValidation() {
        HabitInputDTO dto = HabitInputDTO.builder()
                .name("   ")
                .category("MENTAL")
                .build();

        Set<ConstraintViolation<HabitInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void tooLongName_failsValidation() {
        String longName = "a".repeat(101);
        HabitInputDTO dto = HabitInputDTO.builder()
                .name(longName)
                .category("MENTAL")
                .build();

        Set<ConstraintViolation<HabitInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void blankCategory_failsValidation() {
        HabitInputDTO dto = HabitInputDTO.builder()
                .name("Read book")
                .category("   ")
                .build();

        Set<ConstraintViolation<HabitInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("category"));
    }

    @Test
    void tooLongCategory_failsValidation() {
        String longCategory = "x".repeat(21);
        HabitInputDTO dto = HabitInputDTO.builder()
                .name("Meditation")
                .category(longCategory)
                .build();

        Set<ConstraintViolation<HabitInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("category"));
    }

    @Test
    void tooLongDescription_failsValidation() {
        String longDescription = "y".repeat(501);
        HabitInputDTO dto = HabitInputDTO.builder()
                .name("Yoga")
                .category("PHYSICAL")
                .description(longDescription)
                .build();

        Set<ConstraintViolation<HabitInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }
}
