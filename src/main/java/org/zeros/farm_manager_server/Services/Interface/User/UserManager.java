package org.zeros.farm_manager_server.Services.Interface.User;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;

import java.util.UUID;

public interface UserManager {


    UserDTO getUserById(@NotNull UUID id);

    UserDTO getUserByEmail(@NotNull String email);

    UserDTO getUserByUsername(@NotNull String username);

    Page<UserDTO> getAllUsers(@NotNull int pageNumber);

    UserDTO registerNewUser(@NotNull UserDTO userDTO);

    UserDTO updateUserInfo(@NotNull UserDTO userDTO);

    void deleteAllUserData(@NotNull UUID userId);
}
