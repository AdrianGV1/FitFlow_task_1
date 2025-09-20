package una.ac.cr.FitFlow.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.dto.AuthTokenDTO;
import una.ac.cr.FitFlow.dto.User.UserInputDTO;


public interface UserService {
    UserOutputDTO createUser(UserInputDTO userDTO);
    UserOutputDTO updateUser(Long id, UserInputDTO userDTO);
    void deleteUser(Long id);

    UserOutputDTO findUserById(Long id);
    Page<UserOutputDTO> listUsers(String q, Pageable pageable);
    AuthTokenOutputDTO loginByMail(String email, String password);

}