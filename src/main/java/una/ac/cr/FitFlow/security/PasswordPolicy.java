package una.ac.cr.FitFlow.security;
public interface PasswordPolicy {
    void validate(String rawPassword);
}
