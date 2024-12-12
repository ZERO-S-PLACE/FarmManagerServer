package org.zeros.farm_manager_server.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.repositories.UserRepository;

@Component
public class BootstrapUserFields implements CommandLineRunner {


private final UserRepository userRepository;

    public BootstrapUserFields(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override

    public void run(String... args) throws Exception {
    }


}
