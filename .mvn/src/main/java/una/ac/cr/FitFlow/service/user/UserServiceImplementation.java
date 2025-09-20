package una.ac.cr.FitFlow.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.AuthTokenDTO;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenOutputDTO;
import una.ac.cr.FitFlow.repository.UserRepository;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.dto.User.UserInputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.service.AuthToken.AuthTokenService;
import java.time.LocalDateTime; 
import una.ac.cr.FitFlow.model.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    private UserOutputDTO convertToDto(User user) {
        return UserOutputDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleIds(user.getRoles().stream()
                        .map(Role::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    private User convertToEntity(UserInputDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build();
    }

    @Override
    public UserOutputDTO createUser(UserInputDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("El correo electronico ya esta en uso");
        }
        User newUser = convertToEntity(userDTO);
        userRepository.save(newUser);
        return convertToDto(newUser);
    }

    @Override
    public UserOutputDTO updateUser(Long id, UserInputDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmailAndIdNot(userDTO.getEmail(), id)) {
            throw new IllegalArgumentException("El correo electronico ya esta en uso por otro usuario");
        }

        applyUpdates(existingUser, userDTO);
        userRepository.save(existingUser);
        return convertToDto(existingUser);
    }

    private void applyUpdates(User user, UserInputDTO dto) {
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }

    @Override
    public void deleteUser(Long id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
    }

    @Override
    public UserOutputDTO findUserById(Long id) {
        if(userRepository.existsById(id)) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            return convertToDto(user);
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

    }

    @Override
    public Page<UserOutputDTO> listUsers(String q, Pageable pageable) {
        if (q == null || q.isEmpty()) {
            return userRepository.findAll(pageable).map(this::convertToDto);
        }
        else {
            return userRepository.findByUsernameContainingIgnoreCase(q, pageable)
                    .map(this::convertToDto);
        }
    }

    @Override
    public AuthTokenOutputDTO loginByMail(String email, String password) {
        if(!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Correo electronico no encontrado");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = UUID.randomUUID().toString();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
            AuthTokenDTO newToken = AuthTokenDTO.builder()
                    .token(token)
                    .expiresAt(expiresAt)
                    .userId(user.getId())
                    .build();
            AuthTokenDTO savedToken = authTokenService.create(newToken);
            return AuthTokenOutputDTO.builder()
                    .token(savedToken.getToken())
                    .expiresAt(savedToken.getExpiresAt())
                    .userId(savedToken.getUserId())
                    .build();
        } else {
            throw new IllegalArgumentException("Clave incorrecta");
        }
    }
}
