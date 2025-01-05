package org.zeros.farm_manager_server.Services.Interface.Fields;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;

import java.util.Set;
import java.util.UUID;

public interface FieldManager {
    Field createFieldDefault(@NotNull FieldDTO fieldDTO);

    Field createFieldInGroup(@NotNull FieldDTO fieldDTO, @NotNull UUID groupId);

    Field getFieldById(@NotNull UUID id);

    Set<Field> getAllFields();

    Field updateField(@NotNull FieldDTO fieldDTO);

    void deleteFieldWithData(@NotNull UUID fieldId);

    void archiveField(@NotNull UUID fieldId);

    void deArchiveField(@NotNull UUID fieldId);
}
