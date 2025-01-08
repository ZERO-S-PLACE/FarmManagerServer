package org.zeros.farm_manager_server.Controllers.User;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Services.Default.User.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    public static final String USER_PATH = "/api/admin/user/{id}";
    public static final String USER_DATA_PATH ="/api/user/INFO";
    public final UserManager userManager;
    private final LoggedUserConfiguration loggedUserConfiguration;

    @GetMapping(USER_DATA_PATH)
    public UserDTO getCurrentUserData() {
        User user=loggedUserConfiguration.getLoggedUser();
        if (user.getId() == null) {
            throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return DefaultMappers.userMapper.entityToDto(user);
    }

@GetMapping(USER_PATH)
public UserDTO getUserDataById(@PathVariable("id") UUID id) {
    User user=userManager.getUserById(id);
    if (user.getId() == null) {
        throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
    }
    return DefaultMappers.userMapper.entityToDto(user);
}
/*
 User createNewUser(User user){
    User getUserByEmail(String email);

    User getUserByUsername(String username);

    User logInNewUserByEmailAndPassword(String email, String password);

    User logInNewUserByUsernameAndPassword(String username, String password);

    void logOutUser();

    User updateUserInfo(User user);

    void deleteAllUserData(User user);



}
*/
}