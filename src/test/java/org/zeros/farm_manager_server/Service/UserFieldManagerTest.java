package org.zeros.farm_manager_server.Service;

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
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldGroupDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldPartDTO;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Repositories.UserRepository;
import org.zeros.farm_manager_server.Services.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.UserFieldsManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserFieldsManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserFieldManagerTest {

    @Autowired
    UserFieldsManager userFieldsManager;
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
    private User user;
    @Autowired
    private FieldGroupRepository fieldGroupRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }


    @Test
    void testCreateEmptyFieldGroup() {
        FieldGroup fieldGroup = userFieldsManager.createEmptyFieldGroup("TEST1", "");
        assertThat(fieldGroup).isNotNull();
        assertThat(fieldGroup.getId()).isNotNull();
        assertThat(fieldGroup.getCreatedDate()).isNotNull();
        assertThat(fieldGroup.getLastModifiedDate()).isNotNull();
        assertThat(fieldGroup.getFieldGroupName()).isNotNull();
        User groupUser = userManager.getUserById(user.getId());
        assertThat(groupUser.getFieldGroups()).contains(fieldGroup);
    }

    @Test
    void testUpdateFieldGroup() {
        FieldGroup fieldGroup = userFieldsManager.createEmptyFieldGroup("TEST1", "");
        assertThat(fieldGroup).isNotNull();
        assertThat(fieldGroup.getId()).isNotNull();
        FieldGroupDTO fieldGroupDTO = DefaultMappers.fieldGroupMapper.entityToDto(fieldGroup);
        fieldGroupDTO.setDescription("SOMETHING");
        fieldGroupDTO.setFieldGroupName("TEST1PATCHED");
        FieldGroup fieldGroupPatched = userFieldsManager.updateFieldGroup(fieldGroupDTO);
        assertThat(fieldGroupPatched).isNotNull();
        assertThat(fieldGroupPatched.getId()).isNotNull();
        assertThat(fieldGroupPatched.getFieldGroupName()).isEqualTo("TEST1PATCHED");
        assertThat(userManager.getUserById(user.getId()).getFieldGroups()).contains(fieldGroupPatched);
    }

    @Test
    void testCreateFieldDefault() {
        Field fieldSaved = userFieldsManager.createFieldDefault(createTestField(0));
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
        Field fieldSaved = userFieldsManager.createFieldDefault(createTestField(0));
        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();
        FieldDTO fieldDTO = DefaultMappers.fieldMapper.entityToDto(fieldSaved);
        fieldDTO.setFieldName("UPDATED_11");
        fieldDTO.setDescription("UPDATED_11");
        fieldDTO.setArea(100);
        Field fieldSaved2 = userFieldsManager.updateField(fieldDTO);


        assertThat(fieldRepository.findById(fieldSaved2.getId()).get().getFieldName().equals("UPDATED_11")).isTrue();
        assertThat(fieldSaved2).isNotNull();
        assertThat(fieldSaved2.getArea()).isEqualTo(fieldSaved.getArea());
        assertThat(fieldSaved2.getFieldParts().stream().findFirst().orElse(FieldPart.NONE).getArea()).isEqualTo(fieldSaved.getArea());

        FieldGroup fieldGroup = fieldGroupRepository.findById(fieldSaved2.getFieldGroup().getId()).get();
        User fieldUser = userRepository.findUserById(fieldSaved2.getUser().getId()).get();
        assertThat(fieldGroup).isNotNull();
        assertThat(fieldUser).isNotNull();
    }

    @Test
    void testDeleteFieldGroupWithFields() {
        User fieldUser = userRepository.findUserById(user.getId()).get();
        FieldGroup group= user.getFieldGroups().stream().filter(fieldGroup -> !(fieldGroup.getFieldGroupName().equals("DEFAULT")))
                .toList().get(0);
        Field fieldSaved = userFieldsManager.createFieldInGroup(createTestField(0),group.getId());
        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();

        userFieldsManager.deleteFieldGroupWithFields(fieldSaved.getFieldGroup().getId());

        fieldUser = userRepository.findUserById(user.getId()).get();
        assertThat(fieldRepository.findById(fieldSaved.getId()).isPresent()).isEqualTo(false);
        assertThat(fieldGroupRepository.findById(fieldSaved.getFieldGroup().getId()).isPresent()).isEqualTo(false);
        assertThat(fieldUser.getFields().contains(fieldSaved)).isFalse();
        assertThat(fieldUser.getFieldGroups().contains(fieldSaved.getFieldGroup())).isFalse();
    }

    @Test
    void testDeleteFieldGroupWithoutFields() {
        User fieldUser = userRepository.findUserById(user.getId()).get();
        FieldGroup fieldGroup1 = fieldUser.getFieldGroups().stream().filter(fg -> !fg.getFieldGroupName().equals("DEFAULT")).findFirst().get();
        Field fieldSaved = userFieldsManager.createFieldInGroup(createTestField(0), fieldGroup1.getId());
        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();
        userFieldsManager.deleteFieldGroupWithoutFields(fieldSaved.getFieldGroup().getId());

        fieldUser = userRepository.findUserById(user.getId()).get();
        assertThat(fieldRepository.findById(fieldSaved.getId()).isPresent()).isEqualTo(true);
        assertThat(fieldGroupRepository.findById(fieldGroup1.getId()).isPresent()).isEqualTo(false);
        assertThat(fieldUser.getFields().contains(fieldRepository.findById(fieldSaved.getId()).get())).isTrue();
        assertThat(fieldUser.getFieldGroups().contains(fieldGroup1)).isFalse();
    }

    @Test
    void testArchiveField() {
        Field field = userFieldsManager.createFieldDefault(createTestField(0));
        userFieldsManager.archiveField(field.getId());
        assertThat(fieldRepository.findById(field.getId()).get().getIsArchived()).isEqualTo(true);
    }

    @Test
    void testDivideFieldPart() {
        User fieldUser = userRepository.findUserById(user.getId()).get();
        FieldGroup fieldGroup1 = fieldUser.getFieldGroups().stream().findAny().get();
        Field fieldSaved = userFieldsManager.createFieldInGroup(createTestField(0), fieldGroup1.getId());
        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();

        FieldPart basePart = fieldSaved.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
        FieldPartDTO part1 = createTestFieldPart(0, fieldSaved.getArea().floatValue() * 0.3f);
        FieldPartDTO part2 = createTestFieldPart(1, 1);

        Field dividedField = userFieldsManager.divideFieldPart(basePart.getId(), part1, part2);
        assertThat(dividedField).isNotNull();
        assertThat(dividedField.getFieldParts().contains(fieldPartRepository.findById(basePart.getId()).orElse(FieldPart.NONE))).isTrue();
        assertThat(dividedField.getFieldParts().size()).isEqualTo(3);
        BigDecimal areaSum = BigDecimal.ZERO;
        for (FieldPart fieldPart : dividedField.getFieldParts()) {
            if (!fieldPart.getIsArchived()) {
                areaSum = areaSum.add(fieldPart.getArea());
            }
        }
        assertThat(fieldSaved.getArea().floatValue() == areaSum.floatValue()).isTrue();

    }

    @Test
    void testMergeFieldParts() {
        User fieldUser = userRepository.findUserById(user.getId()).get();
        FieldGroup fieldGroup1 = fieldUser.getFieldGroups().stream().findAny().get();
        Field fieldSaved = userFieldsManager.createFieldInGroup(createTestField(0), fieldGroup1.getId());
        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();

        FieldPart basePart = fieldSaved.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
        FieldPartDTO part1 = createTestFieldPart(0, fieldSaved.getArea().floatValue() * 0.3f);
        FieldPartDTO part2 = createTestFieldPart(1, 1);

        Field dividedField = userFieldsManager.divideFieldPart(basePart.getId(), part1, part2);

        FieldPart merged = userFieldsManager.mergeFieldParts(userFieldsManager.getAllNonArchivedFieldParts(dividedField.getId())
                .stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        dividedField = fieldRepository.findById(dividedField.getId()).orElse(Field.NONE);
        assertThat(merged).isNotNull();
        assertThat(merged.getArea()).isEqualTo(dividedField.getArea());
        assertThat(dividedField.getFieldParts().size()).isEqualTo(4);
        assertThat(userFieldsManager.getAllNonArchivedFieldParts(dividedField.getId()).size()).isEqualTo(1);
        assertThat(userFieldsManager.getAllNonArchivedFieldParts(dividedField.getId())).contains(fieldPartRepository.findById(merged.getId()).orElse(FieldPart.NONE));

    }

    @Test
    void testResizeFieldPartResizeField() {
        User fieldUser = userRepository.findUserById(user.getId()).get();
        FieldGroup fieldGroup1 = fieldUser.getFieldGroups().stream().findAny().get();
        Field fieldSaved = userFieldsManager.createFieldInGroup(createTestField(0), fieldGroup1.getId());
        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();

        FieldPart basePart = fieldSaved.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
        FieldPartDTO part1 = createTestFieldPart(0, fieldSaved.getArea().floatValue() * 0.3f);
        FieldPartDTO part2 = createTestFieldPart(1, 1);

        Field dividedField = userFieldsManager.divideFieldPart(basePart.getId(), part1, part2);

        FieldPart resizePart = userFieldsManager.getAllNonArchivedFieldParts(dividedField.getId()).stream().findFirst().get();
        Field resizedField = userFieldsManager.updateFieldPartAreaResizeField(resizePart.getId(), resizePart.getArea().multiply(BigDecimal.valueOf(0.01)));
        BigDecimal areaSum = BigDecimal.ZERO;
        for (FieldPart fieldPart : dividedField.getFieldParts()) {
            if (!fieldPart.getIsArchived()) {
                areaSum = areaSum.add(fieldPart.getArea());
            }
        }
        assertThat(resizedField.getArea().floatValue()).isEqualTo(areaSum.floatValue());
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

    public FieldPartDTO createTestFieldPart(int i, float area) {
        return FieldPartDTO.builder()
                .fieldPartName("TEST_PART_" + i)
                .area(area)
                .build();
    }

}
