package una.ac.cr.FitFlow.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import una.ac.cr.FitFlow.dto.AuthTokenDTO;
import una.ac.cr.FitFlow.dto.UserDTO;
import una.ac.cr.FitFlow.repository.UserRepository;
import una.ac.cr.FitFlow.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserDTO convertToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("El correo electronico ya esta en uso");
        }
        User newUser = User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .build();
        userRepository.save(newUser);
        return convertToDto(newUser);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!existingUser.getEmail().equals(userDTO.getEmail())) {
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setEmail(userDTO.getEmail());
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
            userRepository.save(existingUser);
            return convertToDto(existingUser);
        } else {
            throw new IllegalArgumentException("El correo electronico ya esta en uso por otro usuario");

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
    public UserDTO findUserById(Long id) {
        if(userRepository.existsById(id)) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            return convertToDto(user);
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

    }

    @Override
    public Page<UserDTO> listUsers(String q, Pageable pageable) {
        if (q == null || q.isEmpty()) {
            return userRepository.findAll(pageable).map(this::convertToDto);
        }
        else {
            return userRepository.findByUsernameContainingIgnoreCase(q, pageable)
                    .map(this::convertToDto);
        }
    }

    @Override
    public AuthTokenDTO loginByMail(String email, String password) {
        if(!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Correo electronico no encontrado");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            return AuthTokenDTO.builder().token(password).build();
        } else {
            throw new IllegalArgumentException("Clave incorrecta");
        }
    }
}
