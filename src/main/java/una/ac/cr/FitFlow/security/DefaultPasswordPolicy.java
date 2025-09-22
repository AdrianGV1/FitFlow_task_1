package una.ac.cr.FitFlow.security;
import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.security.PasswordPolicy;

@Component
public class DefaultPasswordPolicy implements PasswordPolicy {

    @Override
    public void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        if (!rawPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Debe incluir al menos una letra mayúscula.");
        }
        if (!rawPassword.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Debe incluir al menos una letra minúscula.");
        }
        if (!rawPassword.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Debe incluir al menos un dígito.");
        }
    }
}
