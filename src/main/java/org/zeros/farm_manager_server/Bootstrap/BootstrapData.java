package org.zeros.farm_manager_server.Bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.zeros.farm_manager_server.Domain.Mappers.User.UserMapper;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final DefaultSetup defaultSetup;
    private final DemoUserSetup demoUserSetup;
    private final UserManager userManager;


    @Override
    public void run(String... args) {
        defaultSetup.createDefaultDataSet();
        demoUserSetup.createDemoUser();
        //userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }

}
