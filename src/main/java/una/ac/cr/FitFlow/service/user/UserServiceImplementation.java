package una.ac.cr.FitFlow.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.User.UserInputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.User.LoginTokenDTO;
import una.ac.cr.FitFlow.mapper.MapperForUser;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.RoleRepository;
import una.ac.cr.FitFlow.repository.UserRepository;
import una.ac.cr.FitFlow.security.JwtService;
import una.ac.cr.FitFlow.security.PasswordHashService;
import una.ac.cr.FitFlow.security.PasswordPolicy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HabitRepository habitRepository;
    private final JwtService jwtService;
    private final MapperForUser mapper;
    private final PasswordHashService passwordHashService;
    private final PasswordPolicy passwordPolicy; // 🔹 ahora validamos reglas

    private static String normUsername(String s) {
        return s == null ? null : s.trim();
    }

    private static String normEmail(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        if (roleIds == null) return null;
        if (roleIds.isEmpty()) return new HashSet<>();
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

        // 🔹 Validar contraseña según política
        passwordPolicy.validate(password);

        Set<Role> roles  = resolveRoles(in.getRoleIds());
        Set<Habit> habits = resolveHabits(in.getHabitIds());

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(passwordHashService.encode(password)); // 🔹 cifrar
        if (roles != null)  u.setRoles(roles);
        if (habits != null) u.setHabits(habits);

        User saved = userRepository.save(u);
        return toDto(saved);
    }

    @Override
    public UserOutputDTO updateUser(Long id, UserInputDTO in) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: id=" + id));

        String newUsername = normUsername(in.getUsername());
        String newEmail    = normEmail(in.getEmail());
        String newPass     = in.getPassword() == null ? null : in.getPassword().trim();

        if (newEmail != null && !newEmail.equals(u.getEmail())
                && userRepository.existsByEmailAndIdNot(newEmail, id)) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso por otro usuario.");
        }

        if (newUsername != null && !newUsername.equals(u.getUsername())
                && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("El username ya está en uso por otro usuario.");
        }

        if (newUsername != null) u.setUsername(newUsername);
        if (newEmail    != null) u.setEmail(newEmail);

        if (newPass != null && !newPass.isEmpty()) {
            // 🔹 validar reglas antes de cifrar
            passwordPolicy.validate(newPass);
            u.setPassword(passwordHashService.encode(newPass));
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
    @Transactional(readOnly = true)
    public LoginTokenDTO loginByMail(String email, String password) {
        String emailNorm = normEmail(email);

        User user = userRepository.findByEmail(emailNorm)
                .orElseThrow(() -> new IllegalArgumentException("Correo electrónico no encontrado."));

        if (!passwordHashService.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Clave incorrecta.");
        }

        // Roles para el claim (MODULE_PERMISSION)
        java.util.Set<String> roles = (user.getRoles() == null) ? java.util.Set.of()
                : user.getRoles().stream()
                        .map(r -> r.getModule() + "_" + r.getPermission())
                        .collect(Collectors.toSet());

        String jwt = jwtService.generateToken(user.getEmail(), roles);
        java.time.OffsetDateTime expiresAt = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
                .plusSeconds(jwtService.getValidityMillis() / 1000);

        return LoginTokenDTO.builder()
                .token(jwt)
                .expiresAt(expiresAt)
                .userId(user.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserOutputDTO> listStudentsOfCoach(Long coachId, Pageable pageable) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: id=" + coachId));

        Page<User> probe = userRepository.findByCoach_Id(coach.getId(), PageRequest.of(0, 1));
        if (probe.getTotalElements() == 0) {
            throw new IllegalStateException(
                    "El usuario id=" + coachId + " no tiene usuarios asignados (no actúa como coach).");
        }

        Page<User> page = userRepository.findByCoach_Id(coach.getId(), pageable);
        return page.map(this::toDto);
    }

    @Override
    @Transactional
    public UserOutputDTO assignUserToCoach(Long coachId, Long userId) {
        if (coachId == null || userId == null) {
            throw new IllegalArgumentException("coachId y userId son requeridos.");
        }
        if (coachId.equals(userId)) {
            throw new IllegalArgumentException("Un usuario no puede ser su propio coach.");
        }

        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new IllegalArgumentException("Coach no encontrado: id=" + coachId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: id=" + userId));

        user.setCoach(coach);
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserOutputDTO> findUsersByHabitId(Long habitId) {
        return userRepository.findByHabits_Id(habitId)
                .stream()
                .map(this::toDto)
                .toList();
    }
}
