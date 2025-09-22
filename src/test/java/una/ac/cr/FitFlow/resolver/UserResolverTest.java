package una.ac.cr.FitFlow.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.dto.User.LoginTokenDTO;
import una.ac.cr.FitFlow.dto.User.UserInputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserPageDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.Habit.HabitService;
import una.ac.cr.FitFlow.service.role.RoleService;
import una.ac.cr.FitFlow.service.user.UserService;

@ExtendWith(MockitoExtension.class)
class UserResolverTest {

    @Mock private UserService userService;
    @Mock private RoleService roleService;
    @Mock private HabitService habitService;

    private UserResolver newResolver() {
        return new UserResolver(userService, roleService, habitService);
    }

    // ============ QUERIES ============

    @Test
    @DisplayName("userById: requireRead y delega a UserService.findUserById")
    void userById_ok() {
        UserResolver resolver = newResolver();
        Long id = 10L;
        UserOutputDTO dto = mock(UserOutputDTO.class);
        when(userService.findUserById(id)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            UserOutputDTO out = resolver.userById(id);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RUTINAS));
            verify(userService).findUserById(id);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("users: pagina y mapea a UserPageDTO")
    void users_ok() {
        UserResolver resolver = newResolver();
        int page = 1, size = 3;
        String keyword = "gab";

        List<UserOutputDTO> content = List.of(mock(UserOutputDTO.class), mock(UserOutputDTO.class));
        Page<UserOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 7);
        when(userService.listUsers(eq(keyword), eq(PageRequest.of(page, size)))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            UserPageDTO out = resolver.users(page, size, keyword);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RUTINAS));
            verify(userService).listUsers(eq(keyword), eq(PageRequest.of(page, size)));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(7L);
            assertThat(out.getTotalPages()).isEqualTo((int) Math.ceil(7.0 / size));
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    @Test
    @DisplayName("studentsOfCoach: pagina y mapea a UserPageDTO")
    void studentsOfCoach_ok() {
        UserResolver resolver = newResolver();
        Long coachId = 77L;
        int page = 0, size = 2;

        List<UserOutputDTO> content = List.of(mock(UserOutputDTO.class));
        Page<UserOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 1);
        when(userService.listStudentsOfCoach(coachId, PageRequest.of(page, size))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            UserPageDTO out = resolver.studentsOfCoach(coachId, page, size);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.RUTINAS));
            verify(userService).listStudentsOfCoach(coachId, PageRequest.of(page, size));

            assertThat(out.getContent()).isEqualTo(content);
            assertThat(out.getTotalElements()).isEqualTo(1L);
            assertThat(out.getTotalPages()).isEqualTo(1);
            assertThat(out.getPageNumber()).isEqualTo(page);
            assertThat(out.getPageSize()).isEqualTo(size);
        }
    }

    // ============ MUTATIONS ============

    @Test
    @DisplayName("createUser: delega a UserService.createUser (sin requireWrite)")
    void createUser_ok() {
        UserResolver resolver = newResolver();
        UserInputDTO input = mock(UserInputDTO.class);
        UserOutputDTO created = mock(UserOutputDTO.class);
        when(userService.createUser(input)).thenReturn(created);

        // createUser no llama a SecurityUtils en el resolver
        UserOutputDTO out = resolver.createUser(input);

        verify(userService).createUser(input);
        assertThat(out).isSameAs(created);
    }

    @Test
    @DisplayName("updateUser: requireWrite y delega a UserService.updateUser")
    void updateUser_ok() {
        UserResolver resolver = newResolver();
        Long id = 5L;
        UserInputDTO input = mock(UserInputDTO.class);
        UserOutputDTO updated = mock(UserOutputDTO.class);
        when(userService.updateUser(id, input)).thenReturn(updated);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            UserOutputDTO out = resolver.updateUser(id, input);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RUTINAS));
            verify(userService).updateUser(id, input);
            assertThat(out).isSameAs(updated);
        }
    }

    @Test
    @DisplayName("deleteUser: requireWrite, borra y retorna true")
    void deleteUser_ok() {
        UserResolver resolver = newResolver();
        Long id = 9L;

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean out = resolver.deleteUser(id);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RUTINAS));
            verify(userService).deleteUser(id);
            assertThat(out).isTrue();
        }
    }

    @Test
    @DisplayName("login: delega a UserService.loginByMail")
    void login_ok() {
        UserResolver resolver = newResolver();
        String email = "gabo@una.ac.cr";
        String password = "secret";
        LoginTokenDTO token = mock(LoginTokenDTO.class);
        when(userService.loginByMail(email, password)).thenReturn(token);

        LoginTokenDTO out = resolver.login(email, password);

        verify(userService).loginByMail(email, password);
        assertThat(out).isSameAs(token);
    }

    @Test
    @DisplayName("assignUserToCoach: requireWrite y delega a UserService.assignUserToCoach")
    void assignUserToCoach_ok() {
        UserResolver resolver = newResolver();
        Long coachId = 1L, userId = 2L;
        UserOutputDTO dto = mock(UserOutputDTO.class);
        when(userService.assignUserToCoach(coachId, userId)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            UserOutputDTO out = resolver.assignUserToCoach(coachId, userId);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.RUTINAS));
            verify(userService).assignUserToCoach(coachId, userId);
            assertThat(out).isSameAs(dto);
        }
    }

    // ============ FIELD RESOLVERS ============

    @Test
    @DisplayName("@SchemaMapping roles: devuelve lista vacía si no hay IDs")
    void roles_empty_ok() {
        UserResolver resolver = newResolver();
        UserOutputDTO user = mock(UserOutputDTO.class);
        when(user.getRoleIds()).thenReturn(null);

        List<RoleOutputDTO> out = resolver.roles(user);

        assertThat(out).isEmpty();
        verifyNoInteractions(roleService);
    }

    @Test
    @DisplayName("@SchemaMapping roles: mapea roleIds -> RoleOutputDTO (orden indiferente)")
    void roles_mapping_ok() {
        UserResolver resolver = newResolver();
        UserOutputDTO user = mock(UserOutputDTO.class);
        // CORRECCIÓN: usar Set.of(...) porque getRoleIds() devuelve Set<Long>
        when(user.getRoleIds()).thenReturn(Set.of(1L, 2L, 3L));

        RoleOutputDTO r1 = mock(RoleOutputDTO.class);
        RoleOutputDTO r2 = mock(RoleOutputDTO.class);
        RoleOutputDTO r3 = mock(RoleOutputDTO.class);

        when(roleService.findById(1L)).thenReturn(r1);
        when(roleService.findById(2L)).thenReturn(r2);
        when(roleService.findById(3L)).thenReturn(r3);

        List<RoleOutputDTO> out = resolver.roles(user);

        // Como Set no garantiza orden, validar en cualquier orden
        assertThat(out).containsExactlyInAnyOrder(r1, r2, r3);
        verify(roleService).findById(1L);
        verify(roleService).findById(2L);
        verify(roleService).findById(3L);
    }

    @Test
    @DisplayName("@SchemaMapping habits: devuelve lista vacía si no hay IDs")
    void habits_empty_ok() {
        UserResolver resolver = newResolver();
        UserOutputDTO user = mock(UserOutputDTO.class);
        when(user.getHabitIds()).thenReturn(null);

        List<HabitOutputDTO> out = resolver.habits(user);

        assertThat(out).isEmpty();
        verifyNoInteractions(habitService);
    }

    @Test
    @DisplayName("@SchemaMapping habits: mapea habitIds -> HabitOutputDTO (orden indiferente)")
    void habits_mapping_ok() {
        UserResolver resolver = newResolver();
        UserOutputDTO user = mock(UserOutputDTO.class);
        // CORRECCIÓN: usar Set.of(...) porque getHabitIds() devuelve Set<Long>
        when(user.getHabitIds()).thenReturn(Set.of(10L, 20L));

        HabitOutputDTO h1 = mock(HabitOutputDTO.class);
        HabitOutputDTO h2 = mock(HabitOutputDTO.class);

        when(habitService.findHabitById(10L)).thenReturn(h1);
        when(habitService.findHabitById(20L)).thenReturn(h2);

        List<HabitOutputDTO> out = resolver.habits(user);

        assertThat(out).containsExactlyInAnyOrder(h1, h2);
        verify(habitService).findHabitById(10L);
        verify(habitService).findHabitById(20L);
    }
}
