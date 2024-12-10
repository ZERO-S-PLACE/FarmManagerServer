package org.zeros.farm_manager_server.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.UserManager;
import org.zeros.farm_manager_server.entities.User;
import org.zeros.farm_manager_server.repositories.UserRepository;

@Component
public class BootstrapUserFields implements CommandLineRunner {

@Autowired
UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        User user= createTestUser(0);
        User savedUser=userRepository.save(user);
        System.out.println(savedUser.getCreatedDate());
    }

    public static User createTestUser(int userNumber)
    {
        return User.builder()
                .firstName("Test")
                .lastName("User"+userNumber)
                .email("test"+userNumber+"@user.com")
                .username("TestUser"+userNumber)
                .password("password")
                .build();

    }
}
