package una.ac.cr.FitFlow.dto.Role;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import una.ac.cr.FitFlow.model.Role;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoleInputDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_passesValidation() {
        RoleInputDTO dto = RoleInputDTO.builder()
                .id(1L)
                .name("ADMIN_ROLE")
                .permissions(Role.Permission.EDITOR)
                .module(Role.Module.RUTINAS)
                .build();

        Set<ConstraintViolation<RoleInputDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_failsValidation() {
        RoleInputDTO dto = RoleInputDTO.builder()
                .name("   ")
                .permissions(Role.Permission.AUDITOR)
                .module(Role.Module.ACTIVIDADES)
                .build();

        Set<ConstraintViolation<RoleInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void tooLongName_failsValidation() {
        String longName = "X".repeat(51);
        RoleInputDTO dto = RoleInputDTO.builder()
                .name(longName)
                .permissions(Role.Permission.AUDITOR)
                .module(Role.Module.GUIAS)
                .build();

        Set<ConstraintViolation<RoleInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void nullPermissions_failsValidation() {
        RoleInputDTO dto = RoleInputDTO.builder()
                .name("MANAGER_ROLE")
                .module(Role.Module.PROGRESO)
                .build();

        Set<ConstraintViolation<RoleInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("permissions"));
    }

    @Test
    void nullModule_failsValidation() {
        RoleInputDTO dto = RoleInputDTO.builder()
                .name("SUPPORT_ROLE")
                .permissions(Role.Permission.EDITOR)
                .build();

        Set<ConstraintViolation<RoleInputDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("module"));
    }
}
