package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import una.ac.cr.FitFlow.service.user.UserService;
import una.ac.cr.FitFlow.dto.UserDTO;
import una.ac.cr.FitFlow.dto.AuthTokenDTO;

@Controller
@RequiredArgsConstructor
public class UserResolver {
    private final UserService userService;

    @QueryMapping
    public UserDTO userById(@Argument Long id) {
        return userService.findUserById(id);
    }

    @QueryMapping
    public Page<UserDTO> users(@Argument int page, @Argument int size, @Argument String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.listUsers(keyword, pageable);
    }

    @MutationMapping
    public UserDTO createUser(@Argument UserDTO userDto) {
        return userService.createUser(userDto);
    }

    @MutationMapping
    public UserDTO updateUser(@Argument Long id, @Argument UserDTO userDto) {
        return userService.updateUser(id, userDto);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        userService.deleteUser(id);
        return true;
    }

    @MutationMapping
    public AuthTokenDTO login(@Argument String email, @Argument String password) {
        return userService.loginByMail(email, password);
    }
}