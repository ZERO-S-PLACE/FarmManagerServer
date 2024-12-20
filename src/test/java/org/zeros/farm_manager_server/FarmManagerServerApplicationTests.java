package org.zeros.farm_manager_server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.entities.User.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FarmManagerServerApplicationTests {
    @Autowired
    UserManager userManager;

    @Test
    void contextLoads() {
        User user=userManager.logInNewUserByUsernameAndPassword("TestUser1","password");
        assertThat(user.getFields()).hasSize(4);
        assertThat(user.getFieldGroups()).hasSize(2);

    }

}
