package org.zeros.farm_manager_server.Controllers.User;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    public static final String ADMIN_GET_USER_PATH = "/api/admin/user";
    public static final String ADMIN_USER_PATH_ID = ADMIN_GET_USER_PATH + "/{id}";
    public static final String ADMIN_LIST_ALL_USERS_PATH = ADMIN_GET_USER_PATH + "ALL";
    public static final String USER_DATA_PATH = "/api/user/logged_user";
    public static final String REGISTER_PATH = "/api/register";
    public final UserManager userManager;
    private final LoggedUserConfiguration loggedUserConfiguration;

    @GetMapping(USER_DATA_PATH)
    public UserDTO getCurrentUserData() {
        User user = loggedUserConfiguration.getLoggedUser();
        if (user.getId() == null) {
            throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return DefaultMappers.userMapper.entityToDto(user);
    }

    @GetMapping(ADMIN_USER_PATH_ID)
    public UserDTO getUserDataById(@PathVariable("id") UUID id) {
        User user = userManager.getUserById(id);
        if (user.getId() == null) {
            throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return DefaultMappers.userMapper.entityToDto(user);
    }



    @GetMapping(ADMIN_GET_USER_PATH)
    public UserDTO getUserCriteria(@RequestParam(required = false, defaultValue = "") String email,
                                     @RequestParam(required = false,defaultValue = "")String username) {
        User user;
        if(username.isBlank()&&email.isBlank()) {
            throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        if (email.isBlank()) {
           user = userManager.getUserByUsername(username);
        }else {
            user=userManager.getUserByEmail(email);
        }
        if(user.equals(User.NONE))
        {
            throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return DefaultMappers.userMapper.entityToDto(user);
    }

    @GetMapping(ADMIN_LIST_ALL_USERS_PATH)
    public Page<UserDTO> getAllUsers(@RequestParam Integer pageNumber) {
        return userManager.getAllUsers(pageNumber).map(DefaultMappers.userMapper::entityToDto);
    }

    @PostMapping(REGISTER_PATH)
    ResponseEntity<String> addNew(@RequestBody UserDTO userDTO) {
        User saved=userManager.registerNewUser(userDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }

    @PatchMapping(USER_DATA_PATH)
    ResponseEntity<String> update(@RequestBody UserDTO userDTO) {
        userManager.updateUserInfo(userDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(USER_DATA_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        userManager.deleteAllUserData(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}