package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.model.User;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MapperForUser {

    public UserOutputDTO toDto(User u) {
        Set<Long> roleIds = u.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        Set<Long> habitIds = u.getHabits().stream()
                .map(Habit::getId)
                .collect(Collectors.toSet());

        return UserOutputDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .roleIds(roleIds)
                .habitIds(habitIds)
                .build();
    }
}
