package org.zeros.farm_manager_server.UnitTests.Service.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfigurationService;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.LoginError;
import org.zeros.farm_manager_server.Domain.Enum.UserCreationError;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.Services")
@Import(LoggedUserConfigurationService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserManagerTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserManager userManager;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;


    @Test
    void testCreateUser() {
        UserDTO userDTO = createTestUser(0);
        User savedUser = userManager.registerNewUser(userDTO);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedDate()).isNotNull();
        assertThat(savedUser.getLastModifiedDate()).isNotNull();
        assertThat(savedUser.getVersion()).isNotNull();
        assertThat(userRepository.findById(savedUser.getId()).get()).isEqualTo(savedUser);
    }

    @Test
    void testCreateUserMissingEmail() {
        UserDTO userDTO = createTestUser(3);
        userDTO.setEmail("");
        User savedUser = userManager.registerNewUser(userDTO);
        assertThat(savedUser).isEqualTo(User.NONE);
        assertThat(savedUser.getUserCreationError()).isEqualTo(UserCreationError.EMAIL_MISSING);
    }

    @Test
    void testCreateUserEmailNotUnique() {
        UserDTO userDTO = createTestUser(3);
        userManager.registerNewUser(userDTO);
        UserDTO userNotUniqueDTO = createTestUser(3);
        userNotUniqueDTO.setUsername("USER_NOT_UNIQUE");
        User savedUser = userManager.registerNewUser(userNotUniqueDTO);
        assertThat(savedUser).isEqualTo(User.NONE);
        assertThat(savedUser.getUserCreationError()).isEqualTo(UserCreationError.EMAIL_NOT_UNIQUE);
    }

    @Test
    void testCreateUserUsernameNotUnique() {
        UserDTO userDTO = createTestUser(3);
        userManager.registerNewUser(userDTO);
        UserDTO userNotUniqueDTO = createTestUser(3);
        userNotUniqueDTO.setEmail("NotUnique@gmail.com");
        User savedUser = userManager.registerNewUser(userNotUniqueDTO);
        assertThat(savedUser).isEqualTo(User.NONE);
        assertThat(savedUser.getUserCreationError()).isEqualTo(UserCreationError.USERNAME_NOT_UNIQUE);
    }



    @Test
    void testDeleteAllUserData() {
        UserDTO userDTO = createTestUser(0);
        User savedUser = userManager.registerNewUser(userDTO);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        userManager.deleteAllUserData(savedUser.getId());
        assertThat(userRepository.findById(savedUser.getId()).isPresent()).isEqualTo(false);
    }

    public UserDTO createTestUser(int userNumber) {
        return UserDTO.builder()
                .firstName("Test")
                .lastName("User" + userNumber)
                .email("test" + userNumber + "@user.com")
                .username("TestUser" + userNumber)
                .password("password")
                .build();

    }

}
