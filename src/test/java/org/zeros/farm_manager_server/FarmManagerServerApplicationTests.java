package org.zeros.farm_manager_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FarmManagerServerApplicationTests {
    @Autowired
    UserManager userManager;

}
