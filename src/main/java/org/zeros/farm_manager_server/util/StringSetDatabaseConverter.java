package org.zeros.farm_manager_server.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class StringSetDatabaseConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return String.join("''", attribute);
    }


    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(dbData.split("''")).collect(Collectors.toSet());
    }

}
