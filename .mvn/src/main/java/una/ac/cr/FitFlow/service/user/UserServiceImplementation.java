package una.ac.cr.FitFlow.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenInputDTO;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserInputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForUser;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.RoleRepository;
import una.ac.cr.FitFlow.repository.UserRepository;
import una.ac.cr.FitFlow.service.AuthToken.AuthTokenService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HabitRepository habitRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final MapperForUser mapper;

    // ===========================
    // Helpers
    // ===========================
    private static String normUsername(String s) {
        return s == null ? null : s.trim();
    }

    private static String normEmail(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        if (roleIds == null) return null;               // null = no tocar
        if (roleIds.isEmpty()) return new HashSet<>();  // vacío = limpiar
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new IllegalArgumentException("Uno o más roleIds no existen.");
        }
        return new HashSet<>(roles);
    }

    private Set<Habit> resolveHabits(Set<Long> habitIds) {
        if (habitIds == null) return null;
        if (habitIds.isEmpty()) return new HashSet<>();
        List<Habit> habits = habitRepository.findAllById(habitIds);
        if (habits.size() != habitIds.size()) {
            throw new IllegalArgumentException("Uno o más habitIds no existen.");
        }
        return new HashSet<>(habits);
    }

    private UserOutputDTO toDto(User u) {
        return mapper.toDto(u);
    }

    // ===========================
    // Commands
    // ===========================
    @Override
    public UserOutputDTO createUser(UserInputDTO in) {

        String username = normUsername(in.getUsername());
        String email    = normEmail(in.getEmail());
        String password = in.getPassword() == null ? null : in.getPassword().trim();

        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("username es requerido.");
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("email es requerido.");
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("password es requerido.");
        if (in.getRoleIds() == null || in.getRoleIds().isEmpty())
            throw new IllegalArgumentException("roleIds es requerido y no puede estar vacío.");

        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("El correo electrónico ya está en uso.");
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("El username ya está en uso.");

        Set<Role> roles  = resolveRoles(in.getRoleIds());
        Set<Habit> habits = resolveHabits(in.getHabitIds()); // opcional

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        if (roles != null)  u.setRoles(roles);
        if (habits != null) u.setHabits(habits);

        User saved = userRepository.save(u);
        return toDto(saved);
    }

    @Override
    public UserOutputDTO updateUser(Long id, UserInputDTO in) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: id=" + id));

        // Normalizo antes de comparar/guardar
        String newUsername = normUsername(in.getUsername());
        String newEmail    = normEmail(in.getEmail());
        String newPass     = in.getPassword() == null ? null : in.getPassword().trim();

        // Email cambiado y en uso por otro
        if (newEmail != null && !newEmail.equals(u.getEmail())
                && userRepository.existsByEmailAndIdNot(newEmail, id)) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso por otro usuario.");
        }

        // Username cambiado y en uso por otro
        if (newUsername != null && !newUsername.equals(u.getUsername())
                && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("El username ya está en uso por otro usuario.");
        }

        if (newUsername != null) u.setUsername(newUsername);
        if (newEmail    != null) u.setEmail(newEmail);
        if (newPass != null && !newPass.isEmpty()) {
            u.setPassword(passwordEncoder.encode(newPass));
        }

        Set<Role> roles = resolveRoles(in.getRoleIds());
        if (roles != null) u.setRoles(roles);

        Set<Habit> habits = resolveHabits(in.getHabitIds());
        if (habits != null) u.setHabits(habits);

        User saved = userRepository.save(u);
        return toDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new IllegalArgumentException("Usuario no encontrado: id=" + id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserOutputDTO findUserById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: id=" + id));
        return toDto(u);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserOutputDTO> listUsers(String keyword, Pageable pageable) {
        Page<User> page = (keyword == null || keyword.trim().isEmpty())
                ? userRepository.findAll(pageable)
                : userRepository.findByUsernameContainingIgnoreCase(keyword.trim(), pageable);
        return page.map(this::toDto);
    }

    @Override
    @Transactional
    public AuthTokenOutputDTO loginByMail(String email, String password) {
        String emailNorm = normEmail(email);

        User user = userRepository.findByEmail(emailNorm)
                .orElseThrow(() -> new IllegalArgumentException("Correo electrónico no encontrado."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Clave incorrecta.");
        }

        String token = java.util.UUID.randomUUID().toString();
        java.time.LocalDateTime expiresAt = java.time.LocalDateTime.now().plusHours(1);

        AuthTokenInputDTO input = AuthTokenInputDTO.builder()
                .token(token)
                .expiresAt(expiresAt)
                .userId(user.getId())
                .build();

        return authTokenService.create(input);
    }
}
