package una.ac.cr.FitFlow.dto.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserInputDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_passesValidation() {
        UserInputDTO dto = UserInputDTO.builder()
                .id(1L)
                .username("testuser")
                .password("StrongPass123")
                .email("user@example.com")
                .roleIds(Set.of(1L, 2L))
                .habitIds(Set.of(10L, 20L))
                .build();

        Set<ConstraintViolation<UserInputDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankUsername_failsValidation() {
        UserInputDTO dto = UserInputDTO.builder()
                .username(" ")
                .password("StrongPass123")
                .email("user@example.com")
                .roleIds(Set.of(1L))
                .build();

        Set<ConstraintViolation<UserInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    void shortPassword_failsValidation() {
        UserInputDTO dto = UserInputDTO.builder()
                .username("testuser")
                .password("123") // demasiado corta
                .email("user@example.com")
                .roleIds(Set.of(1L))
                .build();

        Set<ConstraintViolation<UserInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void invalidEmail_failsValidation() {
        UserInputDTO dto = UserInputDTO.builder()
                .username("testuser")
                .password("StrongPass123")
                .email("invalid-email")
                .roleIds(Set.of(1L))
                .build();

        Set<ConstraintViolation<UserInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void emptyRoleIds_failsValidation() {
        UserInputDTO dto = UserInputDTO.builder()
                .username("testuser")
                .password("StrongPass123")
                .email("user@example.com")
                .roleIds(Set.of()) // vacío
                .build();

        Set<ConstraintViolation<UserInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("roleIds"));
    }

    @Test
    void invalidHabitIds_failsValidation() {
        UserInputDTO dto = UserInputDTO.builder()
                .username("testuser")
                .password("StrongPass123")
                .email("user@example.com")
                .roleIds(Set.of(1L))
                .habitIds(Set.of(-5L)) // inválido
                .build();

        Set<ConstraintViolation<UserInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("habitIds"));
    }
}
