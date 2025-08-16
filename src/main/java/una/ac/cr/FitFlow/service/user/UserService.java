package una.ac.cr.FitFlow.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.AuthTokenDTO;
import una.ac.cr.FitFlow.dto.UserDTO;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id,UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO findUserById(Long id);
    Page<UserDTO> listUsers(String q, Pageable pageable);
    AuthTokenDTO loginByMail(String email, String password);
}