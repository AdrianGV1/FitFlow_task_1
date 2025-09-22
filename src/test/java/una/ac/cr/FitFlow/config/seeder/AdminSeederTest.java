package una.ac.cr.FitFlow.config.seeder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.RoleRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminSeederTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    // No usamos la inyección de Spring; creamos el objeto y seteamos @Value por reflexión
    @InjectMocks
    private AdminSeeder seeder;

    // Valores simulando tus application.properties
    private final String adminEmail = "admin@test.com";
    private final String adminUsername = "superadmin";
    private final String adminPassword = "StrongP@ss1";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(seeder, "adminEmail", adminEmail);
        ReflectionTestUtils.setField(seeder, "adminUsername", adminUsername);
        ReflectionTestUtils.setField(seeder, "adminPassword", adminPassword);

        when(passwordEncoder.encode(anyString()))
                .thenAnswer(inv -> "ENC(" + inv.getArgument(0, String.class) + ")");
    }

    @Test
    void run_createsAdminAndAssignsAllRoles_whenAdminDoesNotExist() throws Exception {
        // 1) No existe el admin
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.empty());

        // Guardado inicial de usuario nuevo
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0, User.class);
                    // simula que JPA le asigna un id
                    // (si tu entidad tiene setId/Long, puedes setearlo aquí)
                    return u;
                });

        // 2) No existen los roles aún -> se crean todos
        when(roleRepository.findByModuleAndPermission(any(Role.Module.class), any(Role.Permission.class)))
                .thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class)))
                .thenAnswer(inv -> inv.getArgument(0, Role.class));

        // Act
        seeder.run();

        // Assert: se creó el usuario con username/email/password esperados
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeastOnce()).save(userCaptor.capture());
        User finalSaved = userCaptor.getValue();
        assertThat(finalSaved.getEmail()).isEqualTo(adminEmail);
        assertThat(finalSaved.getUsername()).isEqualTo(adminUsername);
        assertThat(finalSaved.getPassword()).isEqualTo("ENC(" + adminPassword + ")");

        // Debe tener todos los módulos * 2 permisos (EDITOR y AUDITOR)
        int expectedRoles = Role.Module.values().length * 2;
        assertThat(finalSaved.getRoles()).hasSize(expectedRoles);

        // Verifica que se intentó crear/asegurar cada rol
        for (Role.Module m : Role.Module.values()) {
            verify(roleRepository).findByModuleAndPermission(m, Role.Permission.EDITOR);
            verify(roleRepository).findByModuleAndPermission(m, Role.Permission.AUDITOR);
        }

        // Y se guardó el usuario al final
        verify(userRepository, atLeastOnce()).save(any(User.class));
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void run_updatesExistingAdmin_andEnsuresRoles() throws Exception {
        // Admin ya existe con datos distintos
        User existing = new User();
        existing.setEmail(adminEmail);
        existing.setUsername("oldName");
        existing.setPassword("ENC(oldPass)");
        existing.setRoles(new HashSet<>()); // vacío para que haya que asignar

        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0, User.class));

        // Simula que algunos roles ya existen en base de datos (puedes alternar)
        when(roleRepository.findByModuleAndPermission(any(Role.Module.class), any(Role.Permission.class)))
                .thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class)))
                .thenAnswer(inv -> inv.getArgument(0, Role.class));

        // Act
        seeder.run();

        // Assert: username y password actualizados
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeastOnce()).save(userCaptor.capture());
        User finalSaved = userCaptor.getValue();
        assertThat(finalSaved.getUsername()).isEqualTo(adminUsername);
        assertThat(finalSaved.getPassword()).isEqualTo("ENC(" + adminPassword + ")");

        // Roles completos
        int expectedRoles = Role.Module.values().length * 2;
        assertThat(finalSaved.getRoles()).hasSize(expectedRoles);

        // Combinaciones módulo-permiso presentes en el set
        Set<String> pairSet = finalSaved.getRoles().stream()
                .map(r -> r.getModule().name() + ":" + r.getPermission().name())
                .collect(Collectors.toSet());

        Set<String> expectedPairs = new HashSet<>();
        for (Role.Module m : Role.Module.values()) {
            expectedPairs.add(m.name() + ":" + Role.Permission.EDITOR.name());
            expectedPairs.add(m.name() + ":" + Role.Permission.AUDITOR.name());
        }
        assertThat(pairSet).isEqualTo(expectedPairs);
    }
}
