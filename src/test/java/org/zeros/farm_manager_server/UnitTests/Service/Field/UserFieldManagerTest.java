package org.zeros.farm_manager_server.UnitTests.Service.Field;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Repositories.UserRepository;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldGroupManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldPartManagerDefault;
import org.zeros.farm_manager_server.Services.Default.User.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@Import({FieldManagerDefault.class,FieldPartManagerDefault.class, FieldGroupManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserFieldManagerTest {

    @Autowired
    FieldManager fieldManager;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    FieldGroupRepository fieldGroupRepositoryRepository;
    @Autowired
    FieldPartRepository fieldPartRepository;
    @Autowired
    UserManager userManager;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    EntityManager entityManager;
    @Autowired
    private FieldGroupRepository fieldGroupRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }


    @Test
    void testCreateFieldDefault() {
        Field fieldSaved = fieldManager.createFieldDefault(createTestField(0));
        FieldGroup fieldGroup = fieldGroupRepository.findById(fieldSaved.getFieldGroup().getId()).get();
        User fieldUser = userRepository.findUserById(fieldSaved.getUser().getId()).get();


        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();
        assertThat(fieldSaved.getCreatedDate()).isNotNull();
        assertThat(fieldSaved.getLastModifiedDate()).isNotNull();
        assertThat(fieldSaved.getFieldGroup().getFieldGroupName()).isEqualTo("DEFAULT");
        assertThat(fieldUser.getFieldGroups()).contains(fieldGroup);
        assertThat(fieldUser.getFields()).contains(fieldSaved);
        assertThat(fieldSaved.getFieldGroup().getFields()).contains(fieldSaved);
        assertThat(fieldUser.getFields().size()).isEqualTo(6);
        assertThat(fieldUser.getFieldGroups().size()).isEqualTo(3);
        assertThat(fieldSaved.getFieldParts().stream().findFirst().orElse(FieldPart.NONE).getArea()).isEqualTo(fieldSaved.getArea());
    }

    @Test
    void testUpdateField() {
        Field fieldSaved = fieldManager.createFieldDefault(createTestField(0));


        FieldDTO fieldDTO = DefaultMappers.fieldMapper.entityToDto(fieldSaved);
        fieldDTO.setFieldName("UPDATED_11");
        fieldDTO.setDescription("UPDATED_11");
        fieldDTO.setArea(100);
        Field fieldSaved2 = fieldManager.updateField(fieldDTO);
        FieldGroup fieldGroup = fieldGroupRepository.findById(fieldSaved2.getFieldGroup().getId()).get();
        User fieldUser = userRepository.findUserById(fieldSaved2.getUser().getId()).get();


        assertThat(fieldRepository.findById(fieldSaved2.getId()).get().getFieldName().equals("UPDATED_11")).isTrue();
        assertThat(fieldSaved2).isNotNull();
        assertThat(fieldSaved2.getArea()).isEqualTo(fieldSaved.getArea());
        assertThat(fieldSaved2.getFieldParts().stream().findFirst().orElse(FieldPart.NONE).getArea()).isEqualTo(fieldSaved.getArea());
        assertThat(fieldGroup).isNotNull();
        assertThat(fieldUser).isNotNull();
    }

    @Test
    void testArchiveField() {
        Field field = fieldManager.createFieldDefault(createTestField(0));

        fieldManager.archiveField(field.getId());

        assertThat(fieldRepository.findById(field.getId()).get().getIsArchived()).isEqualTo(true);
    }

    public FieldDTO createTestField(int fieldNumber) {
        Random random = new Random();
        return FieldDTO.builder()
                .area(random.nextFloat() * 100)
                .fieldName("TestField" + fieldNumber)
                .isOwnField(true)
                .isArchived(false)
                .propertyTax(random.nextFloat() * 100)
                .build();

    }

}
