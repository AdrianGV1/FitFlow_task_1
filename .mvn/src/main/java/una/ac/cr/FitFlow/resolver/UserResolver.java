package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import una.ac.cr.FitFlow.dto.User.UserInputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserPageDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenOutputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;

import una.ac.cr.FitFlow.service.user.UserService;
import una.ac.cr.FitFlow.service.role.RoleService;
import una.ac.cr.FitFlow.service.Habit.HabitService;

@Controller
@RequiredArgsConstructor
@Validated
public class UserResolver {

    private final UserService userService;
    private final RoleService roleService;
    private final HabitService habitService;

    // ===== Queries =====

    @QueryMapping(name = "userById")
    public UserOutputDTO userById(@Argument("id") Long id) {
        return userService.findUserById(id);
    }

    @QueryMapping(name = "users")
    public UserPageDTO users(@Argument("page") int page,
                             @Argument("size") int size,
                             @Argument("keyword") String keyword) {
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

    // ===== Mutations =====

    @MutationMapping(name = "createUser")
    public UserOutputDTO createUser(@Argument("input") @Valid UserInputDTO userDto) {
        return userService.createUser(userDto);
    }

    @MutationMapping(name = "updateUser")
    public UserOutputDTO updateUser(@Argument("id") Long id,
                                    @Argument("input") UserInputDTO userDto) {
        return userService.updateUser(id, userDto);
    }

    @MutationMapping(name = "deleteUser")
    public Boolean deleteUser(@Argument("id") Long id) {
        userService.deleteUser(id);
        return true;
    }

    @MutationMapping(name = "login")
    public AuthTokenOutputDTO login(@Argument("email") String email,
                                    @Argument("password") String password) {
        return userService.loginByMail(email, password);
    }

    // ===== Field Resolvers =====

    @SchemaMapping(typeName = "User", field = "roles")
    public List<RoleOutputDTO> roles(UserOutputDTO user) {
        if (user.getRoleIds() == null || user.getRoleIds().isEmpty()) {
            return Collections.emptyList();
        }
        return user.getRoleIds().stream()
                .map(roleService::findById)
                .collect(Collectors.toList());
    }

    @SchemaMapping(typeName = "User", field = "habits")
    public List<HabitOutputDTO> habits(UserOutputDTO user) {
        // Requiere que UserOutputDTO tenga getHabitIds()
        if (user.getHabitIds() == null || user.getHabitIds().isEmpty()) {
            return Collections.emptyList();
        }
        return user.getHabitIds().stream()
                .map(habitService::findHabitById)
                .collect(Collectors.toList());
    }
}
