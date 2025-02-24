package org.zeros.farm_manager_server.service_tests.field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.configuration.LoggedUserConfigurationForServiceTest;
import org.zeros.farm_manager_server.domain.dto.fields.FieldDTO;
import org.zeros.farm_manager_server.domain.dto.fields.FieldGroupDTO;
import org.zeros.farm_manager_server.domain.dto.user.UserDTO;
import org.zeros.farm_manager_server.domain.entities.fields.FieldGroup;
import org.zeros.farm_manager_server.domain.entities.user.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.repositories.fields.FieldGroupRepository;
import org.zeros.farm_manager_server.repositories.fields.FieldRepository;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.interfaces.fields.FieldGroupManager;
import org.zeros.farm_manager_server.services.interfaces.fields.FieldManager;
import org.zeros.farm_manager_server.services.interfaces.user.UserManager;

import java.math.BigDecimal;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.services")
@Import(LoggedUserConfigurationForServiceTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserGroupManagerTest {

    @Autowired
    FieldGroupManager fieldGroupManager;
    @Autowired
    FieldManager fieldManager;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    UserManager userManager;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    private User user;
    @Autowired
    private FieldGroupRepository fieldGroupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    public void setUp() {
        user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
        loggedUserConfiguration.replaceUser(user);
    }


    @Test
    void testCreateEmptyFieldGroup() {
        FieldGroupDTO fieldGroup = fieldGroupManager.createEmptyFieldGroup("TEST1", "");
        assertThat(fieldGroup).isNotNull();
        assertThat(fieldGroup.getId()).isNotNull();
        assertThat(fieldGroup.getFieldGroupName()).isNotNull();
    }

    @Test
    void testUpdateFieldGroup() {
        FieldGroupDTO fieldGroupDTO = fieldGroupManager.createEmptyFieldGroup("TEST1", "");

        fieldGroupDTO.setDescription("SOMETHING");
        fieldGroupDTO.setFieldGroupName("TEST1PATCHED");
        FieldGroupDTO fieldGroupPatched = fieldGroupManager.updateFieldGroup(fieldGroupDTO);

        assertThat(fieldGroupPatched).isNotNull();
        assertThat(fieldGroupPatched.getId()).isNotNull();
        assertThat(fieldGroupPatched.getFieldGroupName()).isEqualTo("TEST1PATCHED");

    }

    @Test

    void testDeleteFieldGroupWithFields() {
        FieldGroup group = user.getFieldGroups().stream().filter(fieldGroup -> !(fieldGroup.getFieldGroupName().equals("DEFAULT")))
                .toList().getFirst();
        FieldDTO fieldSaved = fieldManager.createFieldInGroup(createTestField(0), group.getId());


        fieldGroupManager.deleteFieldGroupWithFields(fieldSaved.getFieldGroup());
        testEntityManager.flush();
        testEntityManager.clear();
        UserDTO fieldUser = userManager.getUserById(user.getId());

        assertThat(fieldRepository.findById(fieldSaved.getId()).isPresent()).isEqualTo(false);
        assertThat(fieldGroupRepository.findById(fieldSaved.getFieldGroup()).isPresent()).isEqualTo(false);
        assertThat(fieldUser.getFields().contains(fieldSaved.getId())).isFalse();
        assertThat(fieldUser.getFieldGroups().contains(fieldSaved.getFieldGroup())).isFalse();
    }

    @Test
    void testDeleteFieldGroupWithoutFields() {
        User fieldUser = userRepository.findUserById(user.getId()).get();
        FieldGroup fieldGroup1 = fieldUser.getFieldGroups().stream().filter(fg -> !fg.getFieldGroupName().equals("DEFAULT")).findFirst().get();
        FieldDTO fieldSaved = fieldManager.createFieldInGroup(createTestField(0), fieldGroup1.getId());


        fieldGroupManager.deleteFieldGroupWithoutFields(fieldSaved.getFieldGroup());
        testEntityManager.flush();
        testEntityManager.clear();
        fieldUser = userRepository.findUserById(user.getId()).get();

        assertThat(fieldRepository.findById(fieldSaved.getId()).isPresent()).isEqualTo(true);
        assertThat(fieldGroupRepository.findById(fieldGroup1.getId()).isPresent()).isEqualTo(false);
        assertThat(fieldUser.getFields().contains(fieldRepository.findById(fieldSaved.getId()).get())).isTrue();
        assertThat(fieldUser.getFieldGroups().contains(fieldGroup1)).isFalse();
    }

    public FieldDTO createTestField(int fieldNumber) {
        Random random = new Random();
        return FieldDTO.builder()
                .area(BigDecimal.valueOf(random.nextFloat() * 100))
                .fieldName("TestField" + fieldNumber)
                .isOwnField(true)
                .isArchived(false)
                .propertyTax(BigDecimal.valueOf(random.nextFloat() * 100))
                .build();

    }


}
