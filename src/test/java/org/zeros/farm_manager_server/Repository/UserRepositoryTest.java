package org.zeros.farm_manager_server.Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Repositories.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    FieldRepository fieldRepository;



    @BeforeEach
    void setUp() {
    }

    @Test
    void testSaveUser() {
        User user = createTestUser(0);
        User testUser = userRepository.saveAndFlush(user);
        assertThat(this.userRepository.findById(testUser.getId()).get()).isEqualTo(testUser);
        assertThat(testUser.getCreatedDate()).isNotNull();
        assertThat(testUser.getLastModifiedDate()).isNotNull();
    }


    public  User createTestUser(int userNumber) {
        return User.builder()
                .firstName("Test")
                .lastName("User" + userNumber)
                .email("test" + userNumber + "@user.com")
                .username("TestUser" + userNumber)
                .password("password")
                .build();

    }


}
