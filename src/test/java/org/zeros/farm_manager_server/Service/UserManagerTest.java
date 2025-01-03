package org.zeros.farm_manager_server.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.User.LoginError;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Entities.User.UserCreationError;
import org.zeros.farm_manager_server.Repositories.UserRepository;
import org.zeros.farm_manager_server.Services.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserFieldsManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserManagerTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserManager userManager;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    private UserManagerDefault userManagerDefault;

    @Test
    void testCreateUser() {
        UserDTO userDTO = createTestUser(0);
        User savedUser = userManager.createNewUser(userDTO);
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
        User savedUser = userManager.createNewUser(userDTO);
        assertThat(savedUser).isEqualTo(User.NONE);
        assertThat(savedUser.getUserCreationError()).isEqualTo(UserCreationError.EMAIL_MISSING);
    }

    @Test
    void testCreateUserEmailNotUnique() {
        UserDTO userDTO = createTestUser(3);
        userManager.createNewUser(userDTO);
        UserDTO userNotUniqueDTO = createTestUser(3);
        userNotUniqueDTO.setUsername("USER_NOT_UNIQUE");
        User savedUser = userManager.createNewUser(userNotUniqueDTO);
        assertThat(savedUser).isEqualTo(User.NONE);
        assertThat(savedUser.getUserCreationError()).isEqualTo(UserCreationError.EMAIL_NOT_UNIQUE);
    }

    @Test
    void testCreateUserUsernameNotUnique() {
        UserDTO userDTO = createTestUser(3);
        userManager.createNewUser(userDTO);
        UserDTO userNotUniqueDTO = createTestUser(3);
        userNotUniqueDTO.setEmail("NotUnique@gmail.com");
        User savedUser = userManager.createNewUser(userNotUniqueDTO);
        assertThat(savedUser).isEqualTo(User.NONE);
        assertThat(savedUser.getUserCreationError()).isEqualTo(UserCreationError.USERNAME_NOT_UNIQUE);
    }

    @Test
    void testLoginUserByUsername() {
        User user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
        assertThat(user).isNotNull();
        assertThat(user.equals(User.NONE)).isFalse();
        assertThat(loggedUserConfiguration.getLoggedUserProperty().get()).isEqualTo(user);
        userManager.logOutUser();
    }

    @Test
    void testLoginUserByEmail() {
        User user = userManager.logInNewUserByEmailAndPassword("demo@zeros.org", "DEMO_PASSWORD");
        assertThat(user).isNotNull();
        assertThat(user.equals(User.NONE)).isFalse();
        assertThat(loggedUserConfiguration.getLoggedUserProperty().get()).isEqualTo(user);
        userManager.logOutUser();
    }

    @Test
    void testLoginUserByEmailInvalidEmail() {
        User user = userManager.logInNewUserByEmailAndPassword("invaild@gmail.com", "password");
        assertThat(user).isNotNull();
        assertThat(loggedUserConfiguration.getLoggedUserProperty().get()).isEqualTo(User.NONE);
        assertThat(user).isEqualTo(User.NONE);
        assertThat(user.getLoginError()).isEqualTo(LoginError.WRONG_EMAIL);
    }

    @Test
    void testLoginUserByEmailInvalidPassword() {
        User user = userManager.logInNewUserByEmailAndPassword("demo@zeros.org", "DEMO_PASSWORD_Invalid");
        assertThat(user).isNotNull();
        assertThat(loggedUserConfiguration.getLoggedUserProperty().get()).isEqualTo(User.NONE);
        assertThat(user).isEqualTo(User.NONE);
        assertThat(user.getLoginError()).isEqualTo(LoginError.WRONG_PASSWORD);
    }

    @Test
    void testLoginUserByUsernameInvalidUsername() {
        User user = userManager.logInNewUserByUsernameAndPassword("NONE", "password");
        assertThat(user).isNotNull();
        assertThat(loggedUserConfiguration.getLoggedUserProperty().get()).isEqualTo(User.NONE);
        assertThat(user).isEqualTo(User.NONE);
        assertThat(user.getLoginError()).isEqualTo(LoginError.WRONG_USERNAME);
    }

    @Test
    void testLogOutUser() {
        User user = userManager.logInNewUserByEmailAndPassword("demo@zeros.org", "DEMO_PASSWORD");
        assertThat(user).isNotNull();
        assertThat(loggedUserConfiguration.getLoggedUserProperty().get()).isEqualTo(user);
        userManager.logOutUser();
        assertThat(loggedUserConfiguration.getLoggedUserProperty().get()).isEqualTo(User.NONE);
    }

    @Test
    void testDeleteAllUserData() {
        UserDTO userDTO = createTestUser(0);
        User savedUser = userManager.createNewUser(userDTO);
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
