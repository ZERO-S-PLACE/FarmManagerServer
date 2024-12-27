package org.zeros.farm_manager_server.DAO;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.DAO.Default.UserFieldsManagerDefault;
import org.zeros.farm_manager_server.DAO.Default.UserManagerDefault;
import org.zeros.farm_manager_server.DAO.Interface.UserFieldsManager;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.TestObject;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.Fields.Field;
import org.zeros.farm_manager_server.entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.entities.Fields.FieldPart;
import org.zeros.farm_manager_server.repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.repositories.UserRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@Import({UserFieldsManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
        fieldGroup.setDescription("SOMETHING");
        fieldGroup.setFieldGroupName("TEST1PATCHED");
        FieldGroup fieldGroupPatched = userFieldsManager.updateFieldGroupAndDescription(fieldGroup);
        assertThat(fieldGroupPatched).isNotNull();
        assertThat(fieldGroupPatched.getId()).isNotNull();
        assertThat(fieldGroupPatched.getFieldGroupName()).isEqualTo("TEST1PATCHED");
        assertThat(userManager.getUserById(user.getId()).getFieldGroups()).contains(fieldGroupPatched);
    }

    @Test
    void testCreateFieldDefault() {
        Field field = TestObject.createTestField(0);
        Field fieldSaved = userFieldsManager.createFieldDefault(field);
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
        Field field = TestObject.createTestField(0);
        Field fieldSaved = userFieldsManager.createFieldDefault(field);
        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();
        entityManager.detach(fieldSaved);
        fieldSaved.setFieldName("UPDATED_11");
        fieldSaved.setDescription("UPDATED_11");
        fieldSaved.setArea(BigDecimal.valueOf(100));
        Field fieldSaved2 = userFieldsManager.updateFieldInfo(fieldSaved);

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
        Field field = TestObject.createTestField(0);
        Field fieldSaved = userFieldsManager.createFieldDefault(field);
        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();
        userFieldsManager.deleteFieldGroupWithFields(fieldSaved.getFieldGroup());
        entityManager.flush();
        entityManager.clear();
        User fieldUser = userRepository.findUserById(user.getId()).get();
        assertThat(fieldRepository.findById(fieldSaved.getId()).isPresent()).isEqualTo(false);
        assertThat(fieldGroupRepository.findById(fieldSaved.getFieldGroup().getId()).isPresent()).isEqualTo(false);
        assertThat(fieldUser.getFields().contains(fieldSaved)).isFalse();
        assertThat(fieldUser.getFieldGroups().contains(fieldSaved.getFieldGroup())).isFalse();
    }

    @Test
    void testDeleteFieldGroupWithoutFields() {
        User fieldUser = userRepository.findUserById(user.getId()).get();
        Field field = TestObject.createTestField(0);
        FieldGroup fieldGroup1 = fieldUser.getFieldGroups().stream().filter(fg -> !fg.getFieldGroupName().equals("DEFAULT")).findFirst().get();
        entityManager.detach(fieldGroup1);
        Field fieldSaved = userFieldsManager.createFieldInGroup(field, fieldGroup1);
        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();
        userFieldsManager.deleteFieldGroupWithoutFields(fieldSaved.getFieldGroup());
        entityManager.flush();
        entityManager.clear();
        fieldUser = userRepository.findUserById(user.getId()).get();
        assertThat(fieldRepository.findById(fieldSaved.getId()).isPresent()).isEqualTo(true);
        assertThat(fieldGroupRepository.findById(fieldGroup1.getId()).isPresent()).isEqualTo(false);
        assertThat(fieldUser.getFields().contains(fieldRepository.findById(fieldSaved.getId()).get())).isTrue();
        assertThat(fieldUser.getFieldGroups().contains(fieldGroup1)).isFalse();
    }

    @Test
    void testArchiveField() {
        Field field = userFieldsManager.createFieldDefault(TestObject.createTestField(0));
        userFieldsManager.archiveField(field);
        assertThat(fieldRepository.findById(field.getId()).get().getIsArchived()).isEqualTo(true);
    }

    @Test
    void testDivideFieldPart() {
        User fieldUser = userRepository.findUserById(user.getId()).get();
        Field field = TestObject.createTestField(0);
        FieldGroup fieldGroup1 = fieldUser.getFieldGroups().stream().findAny().get();
        entityManager.detach(fieldGroup1);
        Field fieldSaved = userFieldsManager.createFieldInGroup(field, fieldGroup1);
        assertThat(fieldSaved).isNotNull();
        assertThat(fieldSaved.getId()).isNotNull();

        FieldPart basePart = fieldSaved.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
        FieldPart part1 = TestObject.createTestFieldPart(0, fieldSaved.getArea().multiply(BigDecimal.valueOf(0.3)));
        FieldPart part2 = TestObject.createTestFieldPart(1, BigDecimal.valueOf(1));

        Field dividedField = userFieldsManager.divideFieldPart(basePart, part1, part2);
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
        Field field = TestObject.createTestField(0);
        FieldGroup fieldGroup1 = fieldUser.getFieldGroups().stream().findAny().get();
        entityManager.detach(fieldGroup1);
        Field fieldSaved = userFieldsManager.createFieldInGroup(field, fieldGroup1);
        FieldPart basePart = fieldSaved.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
        FieldPart part1 = TestObject.createTestFieldPart(0, fieldSaved.getArea().multiply(BigDecimal.valueOf(0.3)));
        FieldPart part2 = TestObject.createTestFieldPart(1, BigDecimal.valueOf(1));
        Field dividedField = userFieldsManager.divideFieldPart(basePart, part1, part2);

        FieldPart merged = userFieldsManager.mergeFieldParts(userFieldsManager.getAllNonArchivedFieldParts(dividedField));
        dividedField = fieldRepository.findById(dividedField.getId()).orElse(Field.NONE);
        assertThat(merged).isNotNull();
        assertThat(merged.getArea()).isEqualTo(dividedField.getArea());
        assertThat(dividedField.getFieldParts().size()).isEqualTo(4);
        assertThat(userFieldsManager.getAllNonArchivedFieldParts(dividedField).size()).isEqualTo(1);
        assertThat(userFieldsManager.getAllNonArchivedFieldParts(dividedField))
                .contains(fieldPartRepository.findById(merged.getId()).orElse(FieldPart.NONE));

    }

    @Test
    void testResizeFieldPartResizeField() {
        User fieldUser = userRepository.findUserById(user.getId()).get();
        Field field = TestObject.createTestField(0);
        FieldGroup fieldGroup1 = fieldUser.getFieldGroups().stream().findAny().get();
        entityManager.detach(fieldGroup1);
        Field fieldSaved = userFieldsManager.createFieldInGroup(field, fieldGroup1);
        FieldPart basePart = fieldSaved.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
        FieldPart part1 = TestObject.createTestFieldPart(0, fieldSaved.getArea().multiply(BigDecimal.valueOf(0.3)));
        FieldPart part2 = TestObject.createTestFieldPart(1, BigDecimal.valueOf(1));
        Field dividedField = userFieldsManager.divideFieldPart(basePart, part1, part2);

        FieldPart resizePart = userFieldsManager.getAllNonArchivedFieldParts(
                dividedField).stream().findFirst().orElse(FieldPart.NONE);
        Field resizedField = userFieldsManager.updateFieldPartAreaResizeField(
                resizePart, resizePart.getArea().multiply(BigDecimal.valueOf(0.01)));
        BigDecimal areaSum = BigDecimal.ZERO;
        for (FieldPart fieldPart : dividedField.getFieldParts()) {
            if (!fieldPart.getIsArchived()) {
                areaSum = areaSum.add(fieldPart.getArea());
            }
        }
        assertThat(resizedField.getArea().floatValue() == areaSum.floatValue()).isTrue();


    }


}
