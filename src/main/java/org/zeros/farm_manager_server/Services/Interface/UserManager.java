package org.zeros.farm_manager_server.Services.Interface;

import org.zeros.farm_manager_server.Domain.Entities.User.User;

import java.util.UUID;

public interface UserManager {

    User createNewUser(User user);

    User getUserById(UUID id);

    User getUserByEmail(String email);

    User getUserByUsername(String username);

    User logInNewUserByEmailAndPassword(String email, String password);

    User logInNewUserByUsernameAndPassword(String username, String password);

    void logOutUser();

    User updateUserInfo(User user);

    void deleteAllUserData(User user);
}
