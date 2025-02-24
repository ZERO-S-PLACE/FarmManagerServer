package org.zeros.farm_manager_server.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local")
public class BootstrapData implements CommandLineRunner {
    private final DefaultSetup defaultSetup;
    private final DemoUserSetup demoUserSetup;


    @Override
    public void run(String... args) {
        defaultSetup.createDefaultDataSet();
        demoUserSetup.createDemoUser();
    }

}
