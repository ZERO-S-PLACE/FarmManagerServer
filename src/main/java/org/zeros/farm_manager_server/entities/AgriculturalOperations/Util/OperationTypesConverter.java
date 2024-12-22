package org.zeros.farm_manager_server.entities.AgriculturalOperations.Util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class OperationTypesConverter implements AttributeConverter<Set<OperationType>, String> {

    @Override
    public String convertToDatabaseColumn(Set<OperationType> set) {
        return set != null ? set.stream().map(Enum::name).collect(Collectors.joining(",")) : "";
    }

    @Override
    public Set<OperationType> convertToEntityAttribute(String dbData) {
        return dbData != null && !dbData.isEmpty()
                ? Arrays.stream(dbData.split(",")).map(OperationType::valueOf).collect(Collectors.toSet())
                : Set.of();
    }
}