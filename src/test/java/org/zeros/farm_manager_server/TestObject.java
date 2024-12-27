package org.zeros.farm_manager_server;

import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.Fields.Field;
import org.zeros.farm_manager_server.entities.Fields.FieldPart;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

public class TestObject {

    public static User createTestUser(int userNumber) {
        return User.builder()
                .firstName("Test")
                .lastName("User" + userNumber)
                .email("test" + userNumber + "@user.com")
                .username("TestUser" + userNumber)
                .password("password")
                .build();

    }

    public static Field createTestField(int fieldNumber) {
        Random random = new Random();
        return Field.builder()
                .area(BigDecimal.valueOf(random.nextDouble() * 100).round(new MathContext(2)))
                .fieldName("TestField" + fieldNumber)
                .isOwnField(true)
                .isArchived(false)
                .propertyTax(BigDecimal.valueOf(random.nextDouble() * 100).round(new MathContext(2)))
                .build();

    }

    public static FieldPart createTestFieldPart(int i, BigDecimal area) {
        return FieldPart.builder()
                .fieldPartName("TEST_PART_" + i)
                .area(area.round(new MathContext(2)))
                .build();
    }
}
