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
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldPartDTO;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Repositories.UserRepository;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldGroupManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldPartManagerDefault;
import org.zeros.farm_manager_server.Services.Default.User.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@Import({FieldPartManagerDefault.class, FieldManagerDefault.class, FieldGroupManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserFieldPartManagerTest {

    @Autowired
    FieldPartManager fieldPartManager;
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
    private User user;
    @Autowired
    private FieldGroupRepository fieldGroupRepository;
    @Autowired
    private UserRepository userRepository;

    Field fieldSaved;
    FieldPart basePart;
    FieldPartDTO part1;
    FieldPartDTO part2;

    @BeforeEach
    public void setUp() {
        User fieldUser = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
        FieldGroup fieldGroup1 = fieldUser.getFieldGroups().stream().findAny().get();
        fieldSaved = fieldManager.createFieldInGroup(createTestField(0), fieldGroup1.getId());
        basePart = fieldSaved.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
        part1 = createTestFieldPart(0, fieldSaved.getArea().floatValue() * 0.3f);
        part2 = createTestFieldPart(1, 1);
    }

    @Test
    void testDivideFieldPart() {

        fieldPartManager.divideFieldPart(basePart.getId(), part1, part2);
        Field dividedField=fieldManager.getFieldById(fieldSaved.getId());
        BigDecimal areaSum = getTotalAreaOfParts(dividedField);

        assertThat(dividedField).isNotNull();
        assertThat(dividedField.getFieldParts().contains(fieldPartRepository.findById(basePart.getId()).orElse(FieldPart.NONE))).isTrue();
        assertThat(dividedField.getFieldParts().size()).isEqualTo(3);
        assertThat(fieldSaved.getArea().floatValue() == areaSum.floatValue()).isTrue();

    }

    private static BigDecimal getTotalAreaOfParts(Field dividedField) {
        BigDecimal areaSum = BigDecimal.ZERO;
        for (FieldPart fieldPart : dividedField.getFieldParts()) {
            if (!fieldPart.getIsArchived()) {
                areaSum = areaSum.add(fieldPart.getArea());
            }
        }
        return areaSum;
    }

    @Test
    void testMergeFieldParts() {

        fieldPartManager.divideFieldPart(basePart.getId(), part1, part2);
        FieldPart merged = fieldPartManager.mergeFieldParts(fieldPartManager.getAllNonArchivedFieldParts(
                fieldSaved.getId())
                .stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        Field mergedField = fieldRepository.findById(fieldSaved.getId()).orElse(Field.NONE);
        
        
        assertThat(merged).isNotNull();
        assertThat(merged.getArea()).isEqualTo(mergedField.getArea());
        assertThat(mergedField.getFieldParts().size()).isEqualTo(4);
        assertThat(fieldPartManager.getAllNonArchivedFieldParts(mergedField.getId()).size()).isEqualTo(1);
        assertThat(fieldPartManager.getAllNonArchivedFieldParts(mergedField.getId()))
                .contains(fieldPartRepository.findById(merged.getId()).orElse(FieldPart.NONE));

    }

    @Test
    void testResizeFieldPartResizeField() {

        fieldPartManager.divideFieldPart(basePart.getId(), part1, part2);
        Field dividedField=fieldManager.getFieldById(fieldSaved.getId());
        FieldPart partToResize = fieldPartManager
                .getAllNonArchivedFieldParts(dividedField.getId()).stream().findFirst().get();

        fieldPartManager.updateFieldPartAreaResizeField(partToResize.getId(),
                partToResize.getArea().multiply(BigDecimal.valueOf(0.01)));
        Field resizedField=fieldManager.getFieldById(fieldSaved.getId());

       assertThat(resizedField.getArea().floatValue()).isEqualTo(getTotalAreaOfParts(resizedField).floatValue());
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
