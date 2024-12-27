package org.zeros.farm_manager_server.Bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootstrapData implements CommandLineRunner {

    private final DefaultSetup defaultSetup;
    private final DemoUserSetup demoUserSetup;

    public BootstrapData(DefaultSetup defaultSetup, DemoUserSetup demoUserSetup) {
        this.defaultSetup = defaultSetup;
        this.demoUserSetup = demoUserSetup;
    }

    @Override
    public void run(String... args) {
        defaultSetup.createDefaultDataSet();
        demoUserSetup.createDemoUser();
    }

}
