package org.zeros.farm_manager_server.DAO;

import org.zeros.farm_manager_server.entities.User;

import java.util.UUID;

public interface UserManager {

    public abstract User createNewUser(User user);

    public abstract User getUserById(UUID id);

    public abstract User getUserByEmail(String email);
    public abstract User getUserByUsername(String username);
    public abstract User setCurrentUserByEmailAndPassword(String email, String password);
    public abstract User setCurrentUserByUsernameAndPassword(String username,String password);
    public abstract User updateUserInfo(User user);
    public abstract User deleteAllUserData(UUID userId);
}
