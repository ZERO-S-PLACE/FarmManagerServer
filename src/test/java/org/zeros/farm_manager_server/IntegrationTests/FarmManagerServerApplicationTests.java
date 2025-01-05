package org.zeros.farm_manager_server.IntegrationTests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

@SpringBootTest
class FarmManagerServerApplicationTests {
    @Autowired
    UserManager userManager;

}
