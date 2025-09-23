package una.ac.cr.FitFlow.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private HabitRepository habitRepository;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private MapperForUser mapper;
    
    @Mock
    private PasswordHashService passwordHashService;
    
    @Mock
    private PasswordPolicy passwordPolicy;

    @InjectMocks
    private UserServiceImplementation userService;

    private User mockUser;
    private UserInputDTO mockUserInput;
    private UserOutputDTO mockUserOutput;
    private Role mockRole;
    private Habit mockHabit;

    @BeforeEach
    void setUp() {
        // Setup mock objects
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("hashedPassword");

        mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setModule(Role.Module.RUTINAS);
        mockRole.setPermission(Role.Permission.EDITOR);

        mockHabit = new Habit();
        mockHabit.setId(1L);

        mockUserInput = new UserInputDTO();
        mockUserInput.setUsername("testuser");
        mockUserInput.setEmail("test@example.com");
        mockUserInput.setPassword("password123");
        mockUserInput.setRoleIds(Set.of(1L));
        mockUserInput.setHabitIds(Set.of(1L));

        mockUserOutput = UserOutputDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
    }

    // Tests for createUser method
    @Test
    @DisplayName("createUser should create user successfully with valid input")
    void createUser_WithValidInput_ShouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(roleRepository.findAllById(Set.of(1L))).thenReturn(List.of(mockRole));
        when(habitRepository.findAllById(Set.of(1L))).thenReturn(List.of(mockHabit));
        when(passwordHashService.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(mapper.toDto(mockUser)).thenReturn(mockUserOutput);

        // When
        UserOutputDTO result = userService.createUser(mockUserInput);

        // Then
        assertNotNull(result);
        assertEquals(mockUserOutput.getId(), result.getId());
        verify(passwordPolicy).validate("password123");
        verify(passwordHashService).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createUser should throw exception when username is null")
    void createUser_WithNullUsername_ShouldThrowException() {
        // Given
        mockUserInput.setUsername(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(mockUserInput)
        );
        assertEquals("username es requerido.", exception.getMessage());
    }

    @Test
    @DisplayName("createUser should throw exception when username is empty")
    void createUser_WithEmptyUsername_ShouldThrowException() {
        // Given
        mockUserInput.setUsername("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(mockUserInput)
        );
        assertEquals("username es requerido.", exception.getMessage());
    }

    @Test
    @DisplayName("createUser should throw exception when email is null")
    void createUser_WithNullEmail_ShouldThrowException() {
        // Given
        mockUserInput.setEmail(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(mockUserInput)
        );
        assertEquals("email es requerido.", exception.getMessage());
    }

    @Test
    @DisplayName("createUser should throw exception when password is null")
    void createUser_WithNullPassword_ShouldThrowException() {
        // Given
        mockUserInput.setPassword(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(mockUserInput)
        );
        assertEquals("password es requerido.", exception.getMessage());
    }

    @Test
    @DisplayName("createUser should throw exception when roleIds is null")
    void createUser_WithNullRoleIds_ShouldThrowException() {
        // Given
        mockUserInput.setRoleIds(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(mockUserInput)
        );
        assertEquals("roleIds es requerido y no puede estar vacío.", exception.getMessage());
    }

    @Test
    @DisplayName("createUser should throw exception when roleIds is empty")
    void createUser_WithEmptyRoleIds_ShouldThrowException() {
        // Given
        mockUserInput.setRoleIds(Set.of());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(mockUserInput)
        );
        assertEquals("roleIds es requerido y no puede estar vacío.", exception.getMessage());
    }

    @Test
    @DisplayName("createUser should throw exception when email already exists")
    void createUser_WithExistingEmail_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(mockUserInput)
        );
        assertEquals("El correo electrónico ya está en uso.", exception.getMessage());
    }

    @Test
    @DisplayName("createUser should throw exception when username already exists")
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(mockUserInput)
        );
        assertEquals("El username ya está en uso.", exception.getMessage());
    }

    @Test
    @DisplayName("createUser should throw exception when role doesn't exist")
    void createUser_WithNonExistentRole_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(roleRepository.findAllById(Set.of(1L))).thenReturn(List.of()); // Empty list

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(mockUserInput)
        );
        assertEquals("Uno o más roleIds no existen.", exception.getMessage());
    }

    @Test
    @DisplayName("createUser should normalize email to lowercase")
    void createUser_WithUppercaseEmail_ShouldNormalizeToLowercase() {
        // Given
        mockUserInput.setEmail("TEST@EXAMPLE.COM");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(roleRepository.findAllById(Set.of(1L))).thenReturn(List.of(mockRole));
        when(habitRepository.findAllById(Set.of(1L))).thenReturn(List.of(mockHabit));
        when(passwordHashService.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(mapper.toDto(mockUser)).thenReturn(mockUserOutput);

        // When
        userService.createUser(mockUserInput);

        // Then
        verify(userRepository).existsByEmail("test@example.com");
    }

    // Tests for updateUser method
    @Test
    @DisplayName("updateUser should update user successfully")
    void updateUser_WithValidInput_ShouldUpdateUserSuccessfully() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.existsByEmailAndIdNot("newemail@example.com", userId)).thenReturn(false);
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(roleRepository.findAllById(Set.of(1L))).thenReturn(List.of(mockRole));
        when(habitRepository.findAllById(Set.of(1L))).thenReturn(List.of(mockHabit));
        when(passwordHashService.encode("newpassword")).thenReturn("newHashedPassword");
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(mapper.toDto(mockUser)).thenReturn(mockUserOutput);

        UserInputDTO updateInput = new UserInputDTO();
        updateInput.setUsername("newusername");
        updateInput.setEmail("newemail@example.com");
        updateInput.setPassword("newpassword");
        updateInput.setRoleIds(Set.of(1L));
        updateInput.setHabitIds(Set.of(1L));

        // When
        UserOutputDTO result = userService.updateUser(userId, updateInput);

        // Then
        assertNotNull(result);
        verify(passwordPolicy).validate("newpassword");
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("updateUser should throw exception when user not found")
    void updateUser_WithNonExistentUser_ShouldThrowException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateUser(userId, mockUserInput)
        );
        assertEquals("Usuario no encontrado: id=999", exception.getMessage());
    }

    @Test
    @DisplayName("updateUser should throw exception when new email already exists")
    void updateUser_WithExistingEmail_ShouldThrowException() {
        // Given
        Long userId = 1L;
        mockUser.setEmail("current@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.existsByEmailAndIdNot("test@example.com", userId)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateUser(userId, mockUserInput)
        );
        assertEquals("El correo electrónico ya está en uso por otro usuario.", exception.getMessage());
    }

    @Test
    @DisplayName("updateUser should not update password when password is null")
    void updateUser_WithNullPassword_ShouldNotUpdatePassword() {
        // Given
        Long userId = 1L;
        mockUserInput.setPassword(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        // Removed unnecessary stubbings since they're not called when password is null
        when(roleRepository.findAllById(Set.of(1L))).thenReturn(List.of(mockRole));
        when(habitRepository.findAllById(Set.of(1L))).thenReturn(List.of(mockHabit));
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(mapper.toDto(mockUser)).thenReturn(mockUserOutput);

        // When
        userService.updateUser(userId, mockUserInput);

        // Then
        verify(passwordPolicy, never()).validate(anyString());
        verify(passwordHashService, never()).encode(anyString());
    }

    // Tests for deleteUser method
    @Test
    @DisplayName("deleteUser should delete user successfully when user exists")
    void deleteUser_WithExistingUser_ShouldDeleteSuccessfully() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("deleteUser should throw exception when user doesn't exist")
    void deleteUser_WithNonExistentUser_ShouldThrowException() {
        // Given
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.deleteUser(userId)
        );
        assertEquals("Usuario no encontrado: id=999", exception.getMessage());
    }

    // Tests for findUserById method
    @Test
    @DisplayName("findUserById should return user when user exists")
    void findUserById_WithExistingUser_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mapper.toDto(mockUser)).thenReturn(mockUserOutput);

        // When
        UserOutputDTO result = userService.findUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(mockUserOutput.getId(), result.getId());
    }

    @Test
    @DisplayName("findUserById should throw exception when user doesn't exist")
    void findUserById_WithNonExistentUser_ShouldThrowException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.findUserById(userId)
        );
        assertEquals("Usuario no encontrado: id=999", exception.getMessage());
    }

    // Tests for listUsers method
    @Test
    @DisplayName("listUsers should return all users when keyword is null")
    void listUsers_WithNullKeyword_ShouldReturnAllUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(mockUser));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(mapper.toDto(mockUser)).thenReturn(mockUserOutput);

        // When
        Page<UserOutputDTO> result = userService.listUsers(null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userRepository).findAll(pageable);
    }

    @Test
    @DisplayName("listUsers should search users when keyword is provided")
    void listUsers_WithKeyword_ShouldSearchUsers() {
        // Given
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(mockUser));
        when(userRepository.findByUsernameContainingIgnoreCase(keyword, pageable)).thenReturn(userPage);
        when(mapper.toDto(mockUser)).thenReturn(mockUserOutput);

        // When
        Page<UserOutputDTO> result = userService.listUsers(keyword, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userRepository).findByUsernameContainingIgnoreCase(keyword, pageable);
    }

    // Tests for loginByMail method
    @Test
    @DisplayName("loginByMail should return token when credentials are valid")
    void loginByMail_WithValidCredentials_ShouldReturnToken() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String jwt = "jwt.token.here";
        long validityMillis = 3600000L; // 1 hour
        
        mockUser.setRoles(Set.of(mockRole));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(passwordHashService.matches(password, "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(eq(email), any())).thenReturn(jwt);
        when(jwtService.getValidityMillis()).thenReturn(validityMillis);

        // When
        LoginTokenDTO result = userService.loginByMail(email, password);

        // Then
        assertNotNull(result);
        assertEquals(jwt, result.getToken());
        assertEquals(mockUser.getId(), result.getUserId());
        assertNotNull(result.getExpiresAt());
    }

    @Test
    @DisplayName("loginByMail should throw exception when email not found")
    void loginByMail_WithInvalidEmail_ShouldThrowException() {
        // Given
        String email = "nonexistent@example.com";
        String password = "password123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.loginByMail(email, password)
        );
        assertEquals("Correo electrónico no encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("loginByMail should throw exception when password is incorrect")
    void loginByMail_WithInvalidPassword_ShouldThrowException() {
        // Given
        String email = "test@example.com";
        String password = "wrongpassword";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(passwordHashService.matches(password, "hashedPassword")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.loginByMail(email, password)
        );
        assertEquals("Clave incorrecta.", exception.getMessage());
    }

    // Tests for listStudentsOfCoach method
    @Test
    @DisplayName("listStudentsOfCoach should return students when coach exists")
    void listStudentsOfCoach_WithValidCoach_ShouldReturnStudents() {
        // Given
        Long coachId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> probePage = new PageImpl<>(List.of(mockUser));
        Page<User> studentsPage = new PageImpl<>(List.of(mockUser));
        
        when(userRepository.findById(coachId)).thenReturn(Optional.of(mockUser));
        when(userRepository.findByCoach_Id(eq(coachId), any(PageRequest.class))).thenReturn(probePage, studentsPage);
        when(mapper.toDto(mockUser)).thenReturn(mockUserOutput);

        // When
        Page<UserOutputDTO> result = userService.listStudentsOfCoach(coachId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("listStudentsOfCoach should throw exception when coach not found")
    void listStudentsOfCoach_WithNonExistentCoach_ShouldThrowException() {
        // Given
        Long coachId = 999L;
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findById(coachId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.listStudentsOfCoach(coachId, pageable)
        );
        assertEquals("Usuario no encontrado: id=999", exception.getMessage());
    }

    @Test
    @DisplayName("listStudentsOfCoach should throw exception when coach has no students")
    void listStudentsOfCoach_WithCoachHavingNoStudents_ShouldThrowException() {
        // Given
        Long coachId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(List.of());
        
        when(userRepository.findById(coachId)).thenReturn(Optional.of(mockUser));
        when(userRepository.findByCoach_Id(eq(coachId), any(PageRequest.class))).thenReturn(emptyPage);

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> userService.listStudentsOfCoach(coachId, pageable)
        );
        assertTrue(exception.getMessage().contains("no tiene usuarios asignados"));
    }

    // Tests for assignUserToCoach method
    @Test
    @DisplayName("assignUserToCoach should assign user to coach successfully")
    void assignUserToCoach_WithValidIds_ShouldAssignSuccessfully() {
        // Given
        Long coachId = 1L;
        Long userId = 2L;
        User coach = new User();
        coach.setId(coachId);
        User user = new User();
        user.setId(userId);
        
        when(userRepository.findById(coachId)).thenReturn(Optional.of(coach));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(mockUserOutput);

        // When
        UserOutputDTO result = userService.assignUserToCoach(coachId, userId);

        // Then
        assertNotNull(result);
        verify(userRepository).save(user);
        assertEquals(coach, user.getCoach());
    }

    @Test
    @DisplayName("assignUserToCoach should throw exception when coachId is null")
    void assignUserToCoach_WithNullCoachId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.assignUserToCoach(null, 2L)
        );
        assertEquals("coachId y userId son requeridos.", exception.getMessage());
    }

    @Test
    @DisplayName("assignUserToCoach should throw exception when user tries to be their own coach")
    void assignUserToCoach_WithSameIds_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.assignUserToCoach(1L, 1L)
        );
        assertEquals("Un usuario no puede ser su propio coach.", exception.getMessage());
    }

    @Test
    @DisplayName("assignUserToCoach should throw exception when coach not found")
    void assignUserToCoach_WithNonExistentCoach_ShouldThrowException() {
        // Given
        Long coachId = 999L;
        Long userId = 2L;
        when(userRepository.findById(coachId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.assignUserToCoach(coachId, userId)
        );
        assertEquals("Coach no encontrado: id=999", exception.getMessage());
    }

    // Tests for findUsersByHabitId method
    @Test
    @DisplayName("findUsersByHabitId should return users with specified habit")
    void findUsersByHabitId_WithValidHabitId_ShouldReturnUsers() {
        // Given
        Long habitId = 1L;
        when(userRepository.findByHabits_Id(habitId)).thenReturn(List.of(mockUser));
        when(mapper.toDto(mockUser)).thenReturn(mockUserOutput);

        // When
        List<UserOutputDTO> result = userService.findUsersByHabitId(habitId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockUserOutput.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("findUsersByHabitId should return empty list when no users have the habit")
    void findUsersByHabitId_WithNoUsersHavingHabit_ShouldReturnEmptyList() {
        // Given
        Long habitId = 999L;
        when(userRepository.findByHabits_Id(habitId)).thenReturn(List.of());

        // When
        List<UserOutputDTO> result = userService.findUsersByHabitId(habitId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}