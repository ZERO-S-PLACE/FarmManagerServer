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
import org.zeros.farm_manager_server.domain.dto.BaseEntityDTO;
import org.zeros.farm_manager_server.domain.dto.fields.FieldDTO;
import org.zeros.farm_manager_server.domain.dto.fields.FieldPartDTO;
import org.zeros.farm_manager_server.domain.entities.fields.Field;
import org.zeros.farm_manager_server.domain.entities.fields.FieldGroup;
import org.zeros.farm_manager_server.domain.entities.user.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.repositories.fields.FieldRepository;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.interfaces.fields.FieldManager;
import org.zeros.farm_manager_server.services.interfaces.fields.FieldPartManager;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan("org.zeros.farm_manager_server.services")
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
    @Autowired
    private TestEntityManager testEntityManager;


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

        testEntityManager.flush();
        testEntityManager.clear();
        FieldDTO dividedField = fieldManager.getFieldById(fieldSaved.getId());
        BigDecimal areaSum = getTotalAreaOfParts(dividedField);

        assertThat(dividedField).isNotNull();
        assertThat(dividedField.getFieldParts()).contains(basePart.getId());
        assertThat(dividedField.getFieldParts().size()).isEqualTo(3);
        assertThat(fieldSaved.getArea().floatValue() ).isCloseTo(areaSum.floatValue(), Percentage.withPercentage(0.1));

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
        testEntityManager.flush();
        testEntityManager.clear();


        Field mergedField = fieldRepository.findById(fieldSaved.getId()).orElse(Field.NONE);


        assertThat(merged).isNotNull();
        assertThat(merged.getArea()).isCloseTo(mergedField.getArea(),Percentage.withPercentage(0.2));
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
        testEntityManager.flush();
        testEntityManager.clear();
        FieldDTO resizedField = fieldManager.getFieldById(fieldSaved.getId());

        assertThat(resizedField.getArea().floatValue()).isCloseTo(getTotalAreaOfParts(resizedField).floatValue(),Percentage.withPercentage(0.1));
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
                .area(BigDecimal.valueOf(area).setScale(2, BigDecimal.ROUND_HALF_UP))
                .build();
    }

}
