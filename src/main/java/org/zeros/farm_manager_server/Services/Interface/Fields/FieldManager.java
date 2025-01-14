package org.zeros.farm_manager_server.Services.Interface.Fields;

import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;

import java.util.Set;
import java.util.UUID;

public interface FieldManager {
    FieldDTO  createFieldDefault(@NotNull FieldDTO fieldDTO);

    FieldDTO  createFieldInGroup(@NotNull FieldDTO fieldDTO, @NotNull UUID groupId);

    FieldDTO  getFieldById(@NotNull UUID id);

    Set<FieldDTO > getAllFields();

    FieldDTO  updateField(@NotNull FieldDTO fieldDTO);

    @Transactional(readOnly = true)
    Field getFieldIfExists(UUID fieldId);

    void deleteFieldWithData(@NotNull UUID fieldId);

    void archiveField(@NotNull UUID fieldId);

    void deArchiveField(@NotNull UUID fieldId);
}
