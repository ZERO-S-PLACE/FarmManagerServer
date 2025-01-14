package org.zeros.farm_manager_server.UnitTests.Service.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfigurationForServiceTest;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldManager;

import java.math.BigDecimal;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.Services")
@Import(LoggedUserConfigurationForServiceTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserFieldManagerTest {

    @Autowired
    FieldManager fieldManager;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    private FieldGroupRepository fieldGroupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FieldPartRepository fieldPartRepository;

    @BeforeEach
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
    }


    @Test
    @Transactional
    void testCreateFieldDefault() {
        FieldDTO fieldSavedDTO = fieldManager.createFieldDefault(createTestField(0));
        Field fieldSaved = fieldRepository.findById(fieldSavedDTO.getId()).get();
        FieldGroup fieldGroup = fieldGroupRepository.findById(fieldSavedDTO.getFieldGroup()).get();
        User fieldUser = userRepository.findUserById(fieldSavedDTO.getUser()).get();

        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();
        assertThat(fieldSaved.getCreatedDate()).isNotNull();
        assertThat(fieldSaved.getLastModifiedDate()).isNotNull();
        assertThat(fieldSaved.getFieldGroup().getFieldGroupName()).isEqualTo("DEFAULT");
        assertThat(fieldUser.getFieldGroups()).contains(fieldGroup);
        assertThat(fieldUser.getFields()).contains(fieldSaved);
        assertThat(fieldSaved.getFieldGroup().getFields()).contains(fieldSaved);
        assertThat(fieldUser.getFields().size()).isGreaterThan(1);
        assertThat(fieldUser.getFieldGroups().size()).isGreaterThan(1);
        assertThat(fieldSaved.getFieldParts().stream().findFirst().orElse(FieldPart.NONE).getArea()).isEqualTo(fieldSaved.getArea());
    }

    @Test
    void testUpdateField() {
        FieldDTO fieldDTO = fieldManager.createFieldDefault(createTestField(0));
        fieldDTO.setFieldName("UPDATED_11");
        fieldDTO.setDescription("UPDATED_11");
        FieldDTO fieldSaved2 = fieldManager.updateField(fieldDTO);
        FieldGroup fieldGroup = fieldGroupRepository.findById(fieldSaved2.getFieldGroup()).get();
        User fieldUser = userRepository.findUserById(fieldSaved2.getUser()).get();


        assertThat(fieldRepository.findById(fieldSaved2.getId()).get().getFieldName().equals("UPDATED_11")).isTrue();
        assertThat(fieldSaved2).isNotNull();
        assertThat(fieldPartRepository.findById(fieldSaved2.getFieldParts().stream().findFirst().get()).get()
                .getArea()).isEqualTo(fieldDTO.getArea());
        assertThat(fieldGroup).isNotNull();
        assertThat(fieldUser).isNotNull();
    }

    @Test
    void testArchiveField() {
        FieldDTO field = fieldManager.createFieldDefault(createTestField(0));

        fieldManager.archiveField(field.getId());

        assertThat(fieldRepository.findById(field.getId()).get().getIsArchived()).isEqualTo(true);
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
