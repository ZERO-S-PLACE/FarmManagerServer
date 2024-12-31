package org.zeros.farm_manager_server.Controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeros.farm_manager_server.Entities.User.User;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    public static final String USER_PATH ="/api/admin/user/";
    public static final String USER_PATH_ID =USER_PATH+"{userId}";

    private final UserManagerDefault userManagerDefault;



    /*User createNewUser(User user){

    }*/

    @GetMapping(USER_PATH)
    public User getUserById(@RequestParam UUID userId) throws NoSuchObjectException {
        User user=userManagerDefault.getUserById(userId);
        log.debug("Get Beer by Id - in controller");
        if(user==User.NONE){throw new NoSuchObjectException("User do not exist");}
        return user;
    }


    /*
    User getUserByEmail(String email);

    User getUserByUsername(String username);

    User logInNewUserByEmailAndPassword(String email, String password);

    User logInNewUserByUsernameAndPassword(String username, String password);

    void logOutUser();

    User updateUserInfo(User user);

    void deleteAllUserData(User user);

    */

}
