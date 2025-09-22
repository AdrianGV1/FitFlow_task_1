package una.ac.cr.FitFlow.dto.Guide;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GuideInputDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_passesValidation() {
        GuideInputDTO dto = GuideInputDTO.builder()
                .id(1L)
                .title("Valid title")
                .content("Some valid content")
                .category("MENTAL")
                .recommendedHabitIds(Set.of(1L, 2L))
                .build();

        Set<ConstraintViolation<GuideInputDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankTitle_failsValidation() {
        GuideInputDTO dto = GuideInputDTO.builder()
                .title("   ") // en blanco
                .content("Content")
                .category("DIET")
                .recommendedHabitIds(Set.of(1L))
                .build();

        Set<ConstraintViolation<GuideInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    void tooLongContent_failsValidation() {
        String longContent = "a".repeat(10001);

        GuideInputDTO dto = GuideInputDTO.builder()
                .title("Title")
                .content(longContent)
                .category("PHYSICAL")
                .recommendedHabitIds(Set.of(1L))
                .build();

        Set<ConstraintViolation<GuideInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("content"));
    }

    @Test
    void emptyRecommendedHabits_failsValidation() {
        GuideInputDTO dto = GuideInputDTO.builder()
                .title("Valid")
                .content("Valid")
                .category("SLEEP")
                .recommendedHabitIds(Set.of()) // vacío
                .build();

        Set<ConstraintViolation<GuideInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("recommendedHabitIds"));
    }

    @Test
    void negativeHabitId_failsValidation() {
        GuideInputDTO dto = GuideInputDTO.builder()
                .title("Valid")
                .content("Valid")
                .category("DIET")
                .recommendedHabitIds(Set.of(-1L)) // inválido
                .build();

        Set<ConstraintViolation<GuideInputDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("recommendedHabitIds"));
    }
}
