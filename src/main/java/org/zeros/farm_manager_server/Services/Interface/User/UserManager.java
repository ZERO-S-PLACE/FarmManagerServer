package org.zeros.farm_manager_server.Services.Interface.User;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.User.User;

import java.util.UUID;

public interface UserManager {



    User getUserById(@NotNull UUID id);

    User getUserByEmail(@NotNull String email);

    User getUserByUsername(@NotNull String username);

    Page<User> getAllUsers(@NotNull int pageNumber);

    User registerNewUser(@NotNull UserDTO userDTO);

    User updateUserInfo(@NotNull UserDTO userDTO);

    void deleteAllUserData(@NotNull UUID userId);
}
