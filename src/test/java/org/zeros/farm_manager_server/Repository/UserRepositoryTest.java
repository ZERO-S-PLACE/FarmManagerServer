package org.zeros.farm_manager_server.Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.TestObject;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
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
        User user= TestObject.createTestUser(0);
        User testUser=userRepository.saveAndFlush(user);
        assertThat(this.userRepository.findById(testUser.getId()).get()).isEqualTo(testUser);
        assertThat(testUser.getCreatedDate()).isNotNull();
        assertThat(testUser.getLastModifiedDate()).isNotNull();
    }
    @Test
    void testUserWithFields() {
        User user=TestObject.createTestUser(0);
        User testUser=userRepository.save(user);
        assertThat(this.userRepository.findById(testUser.getId()).get()).isEqualTo(testUser);
        Field field1=TestObject.createTestField(0);
        Field field2=TestObject.createTestField(1);
        field1.setUser(testUser);
        field2.setUser(testUser);
        userRepository.save(testUser);
        Field savedField1=fieldRepository.save(field1);
        Field savedField2=fieldRepository.save(field2);
        assertThat(this.fieldRepository.findById(savedField1.getId()).get()).isEqualTo(savedField1);
        assertThat(this.fieldRepository.findById(savedField2.getId()).get()).isEqualTo(savedField2);
        assertThat(userRepository.findById(testUser.getId()).get().getFields()).isEqualTo(testUser.getFields());


    }



}
