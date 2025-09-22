package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.graphql.data.method.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserInputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserPageDTO;
import una.ac.cr.FitFlow.dto.User.LoginTokenDTO;

import una.ac.cr.FitFlow.model.*;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.role.RoleService;
import una.ac.cr.FitFlow.service.user.UserService;

@Controller
@RequiredArgsConstructor
@Validated
public class UserResolver {

    private static final Role.Module MODULE = Role.Module.RUTINAS;
    private static final Role.Module PROGRESS_MODULE = Role.Module.PROGRESO;

    private final UserService userService;
    private final RoleService roleService;
    private final HabitService habitService;

    @PersistenceContext
    private EntityManager em;

    /* ================== QUERIES USUARIO ================== */

    @QueryMapping(name = "userById")
    public UserOutputDTO userById(@Argument("id") Long id) {
        SecurityUtils.requireRead(MODULE);
        return userService.findUserById(id);
    }

    @QueryMapping(name = "users")
    public UserPageDTO users(@Argument("page") int page,
                             @Argument("size") int size,
                             @Argument("keyword") String keyword) {
        SecurityUtils.requireRead(MODULE);
        Pageable pageable = PageRequest.of(page, size);
        Page<UserOutputDTO> userPage = userService.listUsers(keyword, pageable);
        return UserPageDTO.builder()
                .content(userPage.getContent())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .build();
    }

    /* ================== MUTACIONES USUARIO ================== */

    @MutationMapping(name = "createUser")
    public UserOutputDTO createUser(@Argument("input") @Valid UserInputDTO userDto) {
        return userService.createUser(userDto);
    }

    @MutationMapping(name = "updateUser")
    public UserOutputDTO updateUser(@Argument("id") Long id,
                                    @Argument("input") UserInputDTO userDto) {
        SecurityUtils.requireWrite(MODULE);
        return userService.updateUser(id, userDto);
    }

    @MutationMapping(name = "deleteUser")
    public Boolean deleteUser(@Argument("id") Long id) {
        SecurityUtils.requireWrite(MODULE);
        userService.deleteUser(id);
        return true;
    }

    @MutationMapping(name = "login")
    public LoginTokenDTO login(@Argument("email") String email,
                               @Argument("password") String password) {
        return userService.loginByMail(email, password);
    }

    @MutationMapping(name = "assignUserToCoach")
    public UserOutputDTO assignUserToCoach(@Argument Long coachId,
            @Argument Long userId) {
        SecurityUtils.requireWrite(MODULE);
        return userService.assignUserToCoach(coachId, userId);
    }

    /* ================== FIELD RESOLVERS ================== */

    @SchemaMapping(typeName = "User", field = "roles")
    public List<RoleOutputDTO> roles(UserOutputDTO user) {
        if (user.getRoleIds() == null || user.getRoleIds().isEmpty()) {
            return List.of();
        }
        return user.getRoleIds().stream()
                .map(roleService::findById)
                .collect(Collectors.toList());
    }

    @SchemaMapping(typeName = "User", field = "habits")
    public List<HabitOutputDTO> habits(UserOutputDTO user) {
        if (user.getHabitIds() == null || user.getHabitIds().isEmpty()) {
            return List.of();
        }
        return user.getHabitIds().stream()
                .map(habitService::findHabitById)
                .collect(Collectors.toList());
    }

    @QueryMapping(name = "studentsOfCoach")
    public UserPageDTO studentsOfCoach(@Argument("coachId") Long coachId,
            @Argument("page") int page,
            @Argument("size") int size) {
        SecurityUtils.requireRead(MODULE);

        Pageable pageable = PageRequest.of(page, size);
        Page<UserOutputDTO> userPage = userService.listStudentsOfCoach(coachId, pageable);

        return UserPageDTO.builder()
                .content(userPage.getContent())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .build();
    }
}