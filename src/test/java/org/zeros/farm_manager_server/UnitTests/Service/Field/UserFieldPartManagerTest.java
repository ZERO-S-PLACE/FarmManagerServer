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
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldPartDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.Services")
@Import(LoggedUserConfigurationForServiceTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserFieldPartManagerTest {

    @Autowired
    FieldPartManager fieldPartManager;
    @Autowired
    FieldManager fieldManager;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;

    FieldDTO fieldSaved;
    FieldPartDTO basePart;
    FieldPartDTO part1;
    FieldPartDTO part2;


    @BeforeEach
    @Transactional
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
        FieldGroup fieldGroup1 = user.getFieldGroups().stream().findAny().get();
        fieldSaved = fieldManager.createFieldInGroup(createTestField(0), fieldGroup1.getId());
        basePart = fieldPartManager.getFieldPartById(fieldSaved.getFieldParts().stream().findFirst().orElseThrow());
        part1 = createTestFieldPart(0, fieldSaved.getArea().floatValue() * 0.3f);
        part2 = createTestFieldPart(1, 1);
    }

    @Test
    void testDivideFieldPart() {

        fieldPartManager.divideFieldPart(basePart.getId(), part1, part2);
        FieldDTO dividedField = fieldManager.getFieldById(fieldSaved.getId());
        BigDecimal areaSum = getTotalAreaOfParts(dividedField);

        assertThat(dividedField).isNotNull();
        assertThat(dividedField.getFieldParts()).contains(basePart.getId());
        assertThat(dividedField.getFieldParts().size()).isEqualTo(3);
        assertThat(fieldSaved.getArea().floatValue() == areaSum.floatValue()).isTrue();

    }

    private BigDecimal getTotalAreaOfParts(FieldDTO dividedField) {
        BigDecimal areaSum = BigDecimal.ZERO;
        for (UUID fieldPartId : dividedField.getFieldParts()) {
            FieldPartDTO fieldPartDTO = fieldPartManager.getFieldPartById(fieldPartId);
            if (!fieldPartDTO.getIsArchived()) {
                areaSum = areaSum.add(fieldPartDTO.getArea());
            }
        }
        return areaSum;
    }

    @Test
    void testMergeFieldParts() {

        fieldPartManager.divideFieldPart(basePart.getId(), part1, part2);
        FieldPartDTO merged = fieldPartManager.mergeFieldParts(fieldPartManager.getAllNonArchivedFieldParts(
                        fieldSaved.getId())
                .stream().map(BaseEntityDTO::getId).collect(Collectors.toSet()));
        Field mergedField = fieldRepository.findById(fieldSaved.getId()).orElse(Field.NONE);


        assertThat(merged).isNotNull();
        assertThat(merged.getArea()).isEqualTo(mergedField.getArea());
        assertThat(mergedField.getFieldParts().size()).isEqualTo(4);
        assertThat(fieldPartManager.getAllNonArchivedFieldParts(mergedField.getId()).size()).isEqualTo(1);
        assertThat(fieldPartManager.getAllNonArchivedFieldParts(mergedField.getId()))
                .contains(fieldPartManager.getFieldPartById(merged.getId()));

    }

    @Test
    void testResizeFieldPartResizeField() {

        fieldPartManager.divideFieldPart(basePart.getId(), part1, part2);
        FieldDTO dividedField = fieldManager.getFieldById(fieldSaved.getId());
        FieldPartDTO partToResize = fieldPartManager
                .getAllNonArchivedFieldParts(dividedField.getId()).stream().findFirst().get();

        fieldPartManager.updateFieldPartAreaResizeField(partToResize.getId(),
                partToResize.getArea().multiply(BigDecimal.valueOf(0.01)));
        FieldDTO resizedField = fieldManager.getFieldById(fieldSaved.getId());

        assertThat(resizedField.getArea().floatValue()).isEqualTo(getTotalAreaOfParts(resizedField).floatValue());
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

    public FieldPartDTO createTestFieldPart(int i, float area) {
        return FieldPartDTO.builder()
                .fieldPartName("TEST_PART_" + i)
                .area(BigDecimal.valueOf(area))
                .build();
    }

}
