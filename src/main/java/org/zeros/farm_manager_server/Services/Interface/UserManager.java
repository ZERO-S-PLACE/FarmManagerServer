package org.zeros.farm_manager_server.Services.Interface;

import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.User.User;

import java.util.UUID;

public interface UserManager {

    User createNewUser(UserDTO userDTO);

    User getUserById(UUID id);

    User getUserByEmail(String email);

    User getUserByUsername(String username);

    User logInNewUserByEmailAndPassword(String email, String password);

    User logInNewUserByUsernameAndPassword(String username, String password);

    void logOutUser();

    User updateUserInfo(UserDTO userDTO);

    void deleteAllUserData(UUID userId);
}
