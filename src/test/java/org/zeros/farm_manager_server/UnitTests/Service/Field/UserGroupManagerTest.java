package org.zeros.farm_manager_server.UnitTests.Service.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfigurationForServiceTest;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldGroupDTO;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldGroupManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.math.BigDecimal;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.Services")
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
