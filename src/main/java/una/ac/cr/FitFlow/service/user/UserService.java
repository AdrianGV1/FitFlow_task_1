package una.ac.cr.FitFlow.service.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import una.ac.cr.FitFlow.dto.User.LoginTokenDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserInputDTO;

public interface UserService {
    UserOutputDTO createUser(UserInputDTO userDTO);
    UserOutputDTO updateUser(Long id, UserInputDTO userDTO);
    void deleteUser(Long id);

    UserOutputDTO findUserById(Long id);
    Page<UserOutputDTO> listUsers(String keyword, Pageable pageable);

    LoginTokenDTO loginByMail(String email, String password);
    Page<UserOutputDTO> listStudentsOfCoach(Long coachId, Pageable pageable);
    UserOutputDTO assignUserToCoach(Long coachId, Long userId);
    List<UserOutputDTO> findUsersByHabitId(Long habitId);
    
}
