package org.zeros.farm_manager_server.service_tests.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.configuration.LoggedUserConfigurationForServiceTest;
import org.zeros.farm_manager_server.domain.dto.user.UserDTO;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.interfaces.user.UserManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.services")
@Import(LoggedUserConfigurationForServiceTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserManagerTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserManager userManager;

    @Test
    void testCreateUser() {
        UserDTO userDTO = createTestUser(0);
        UserDTO savedUser = userManager.registerNewUser(userDTO);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getVersion()).isNotNull();
    }

    @Test
    void testCreateUserMissingEmail() {
        UserDTO userDTO = createTestUser(3);
        userDTO.setEmail("");
        assertThrows(IllegalArgumentExceptionCustom.class, () -> userManager.registerNewUser(userDTO));
    }

    @Test
    void testCreateUserEmailNotUnique() {
        UserDTO userDTO = createTestUser(3);
        userManager.registerNewUser(userDTO);
        UserDTO userNotUniqueDTO = createTestUser(3);
        userNotUniqueDTO.setUsername("USER_NOT_UNIQUE");
        assertThrows(IllegalArgumentExceptionCustom.class, () -> userManager.registerNewUser(userNotUniqueDTO));
    }

    @Test
    void testCreateUserUsernameNotUnique() {
        UserDTO userDTO = createTestUser(3);
        userManager.registerNewUser(userDTO);
        UserDTO userNotUniqueDTO = createTestUser(3);
        userNotUniqueDTO.setEmail("NotUnique@gmail.com");
        assertThrows(IllegalArgumentExceptionCustom.class, () -> userManager.registerNewUser(userNotUniqueDTO));
    }


    @Test
    void testDeleteAllUserData() {
        UserDTO userDTO = createTestUser(0);
        UserDTO savedUser = userManager.registerNewUser(userDTO);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        userManager.deleteAllUserData(savedUser.getId());
        assertThat(userRepository.findById(savedUser.getId()).isPresent()).isEqualTo(false);
    }

    public UserDTO createTestUser(int userNumber) {
        return UserDTO.builder()
                .firstName("Test")
                .lastName("User" + userNumber)
                .email("testEmail" + userNumber + "@user.com")
                .username("TestUser" + userNumber)
                .password("password")
                .build();

    }

}
