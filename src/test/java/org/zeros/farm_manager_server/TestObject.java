package org.zeros.farm_manager_server;

import org.zeros.farm_manager_server.entities.User;
import org.zeros.farm_manager_server.entities.fields.Field;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

public class TestObject {

    public static User createTestUser(int userNumber)
    {
        return User.builder()
                .firstName("Test")
                .lastName("User"+userNumber)
                .email("test"+userNumber+"@user.com")
                .username("TestUser"+userNumber)
                .password("password")
                .build();

    }
    public static Field createTestField(int fieldNumber)
    {
        Random random = new Random();
        return Field.builder()
                .area(BigDecimal.valueOf(random.nextDouble()*100).round(new MathContext(2)))
                .fieldName("TestField"+fieldNumber)
                .isOwnField(true)
                .propertyTax(BigDecimal.valueOf(random.nextDouble()*100).round(new MathContext(2)))
                .build();

    }
}
