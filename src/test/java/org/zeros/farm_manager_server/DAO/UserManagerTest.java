package org.zeros.farm_manager_server.DAO;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.TestObject;
import org.zeros.farm_manager_server.entities.User;
import org.zeros.farm_manager_server.repositories.FieldRepository;
import org.zeros.farm_manager_server.repositories.UserRepository;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserManagerDefault.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserManagerTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserManager userManager;
    @Test
    void testCreateUser() {
        User user= TestObject.createTestUser(0);
        User savedUser=userManager.createNewUser(user);

        assertThat(savedUser).isNotNull();

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedDate()).isNotNull();
        assertThat(savedUser.getLastModifiedDate()).isNotNull();
        assertThat(savedUser.getVersion()).isNotNull();

        assertThat(userRepository.findById(savedUser.getId())).isNotNull();
    }


}
