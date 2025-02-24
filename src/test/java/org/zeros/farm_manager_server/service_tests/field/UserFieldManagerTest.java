package org.zeros.farm_manager_server.service_tests.field;

import org.assertj.core.data.Percentage;
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
import org.zeros.farm_manager_server.domain.entities.fields.Field;
import org.zeros.farm_manager_server.domain.entities.fields.FieldGroup;
import org.zeros.farm_manager_server.domain.entities.fields.FieldPart;
import org.zeros.farm_manager_server.domain.entities.user.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.repositories.fields.FieldGroupRepository;
import org.zeros.farm_manager_server.repositories.fields.FieldPartRepository;
import org.zeros.farm_manager_server.repositories.fields.FieldRepository;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.interfaces.fields.FieldManager;

import java.math.BigDecimal;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.services")
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
    @Autowired
    private TestEntityManager testEntityManager;

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
        testEntityManager.flush();
        testEntityManager.clear();
        FieldGroup fieldGroup = fieldGroupRepository.findById(fieldSaved2.getFieldGroup()).get();
        User fieldUser = userRepository.findUserById(fieldSaved2.getUser()).get();
        Field field=fieldRepository.findById(fieldSaved2.getId()).get();


        assertThat(field.getFieldName().equals("UPDATED_11")).isTrue();
        assertThat(fieldSaved2).isNotNull();
        assertThat(fieldPartRepository.findById(fieldSaved2.getFieldParts().stream().findFirst().get()).get()
                .getArea()).isCloseTo(fieldDTO.getArea(), Percentage.withPercentage(0.1));
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
