package org.zeros.farm_manager_server.DAO.Interface;

import org.zeros.farm_manager_server.entities.User.User;

import java.util.UUID;

public interface UserManager {

    public abstract User createNewUser(User user);

    public abstract User getUserById(UUID id);

    public abstract User getUserByEmail(String email);
    public abstract User getUserByUsername(String username);
    public abstract User logInNewUserByEmailAndPassword(String email, String password);
    public abstract User logInNewUserByUsernameAndPassword(String username, String password);
    public abstract void logOutUser();
    public abstract User updateUserInfo(User user);
    public abstract void deleteAllUserData(User user);
}
