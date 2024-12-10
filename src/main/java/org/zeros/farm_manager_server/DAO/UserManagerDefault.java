package org.zeros.farm_manager_server.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.entities.User;
import org.zeros.farm_manager_server.repositories.UserRepository;

import java.util.UUID;
@Component
public class UserManagerDefault implements UserManager {

    private final UserRepository userRepository;

    public UserManagerDefault(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createNewUser(User user) {
       try {
          return userRepository.save(user);
       }catch (Exception e){
           return User.NONE;
       }
    }

    @Override
    public User getUserById(UUID id) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        return null;
    }

    @Override
    public User setCurrentUserByEmailAndPassword(String email, String password) {
        return null;
    }

    @Override
    public User setCurrentUserByUsernameAndPassword(String username, String password) {
        return null;
    }

    @Override
    public User updateUserInfo(User user) {
        return null;
    }

    @Override
    public User deleteAllUserData(UUID userId) {
        return null;
    }
}
