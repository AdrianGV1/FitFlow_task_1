package una.ac.cr.FitFlow.config.seeder;

import java.util.EnumSet;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.RoleRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(value = "app.seed.admin", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.seed.admin.name:admin}")
    private String adminUsername;

    @Value("${app.seed.admin.password:Admin123!}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
     
        User admin = userRepository.findByEmail(adminEmail).orElseGet(() -> {
            User u = new User();
            u.setUsername(adminUsername);      
            u.setEmail(adminEmail);
            u.setPassword(passwordEncoder.encode(adminPassword));
            return userRepository.save(u);
        });

        boolean changed = false;
        if (!adminUsername.equals(admin.getUsername())) { admin.setUsername(adminUsername); changed = true; }
        admin.setPassword(passwordEncoder.encode(adminPassword)); changed = true;
        if (changed) admin = userRepository.save(admin);

        if (admin.getRoles() == null) {
            admin.setRoles(new java.util.HashSet<>());
        }

        for (Role.Module m : Role.Module.values()) {
            ensureRole(admin, m, Role.Permission.EDITOR);
            ensureRole(admin, m, Role.Permission.AUDITOR);
        }

        userRepository.save(admin);
        log.info("AdminSeeder listo: {} con permisos EDITOR y AUDITOR en todos los módulos.", adminEmail);
    }

    private void ensureRole(User admin, Role.Module module, Role.Permission perm) {
        Role role = roleRepository.findByModuleAndPermission(module, perm)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .module(module)
                        .permission(perm)
                        .build()));
        admin.getRoles().add(role);
    }
}
