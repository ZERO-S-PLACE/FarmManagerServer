package org.zeros.farm_manager_server.Services.Interface.User;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.User.User;

import java.util.UUID;

public interface UserManager {

    User createNewUser(@NotNull UserDTO userDTO);

    User getUserById(@NotNull UUID id);

    User getUserByEmail(@NotNull String email);

    User getUserByUsername(@NotNull String username);

    User logInNewUserByEmailAndPassword(@NotNull String email, @NotNull String password);

    User logInNewUserByUsernameAndPassword(@NotNull String username, @NotNull String password);

    void logOutUser();

    User updateUserInfo(@NotNull UserDTO userDTO);

    void deleteAllUserData(@NotNull UUID userId);
}
