package org.zeros.farm_manager_server.services.interfaces.fields;

import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.domain.dto.fields.FieldDTO;
import org.zeros.farm_manager_server.domain.entities.fields.Field;

import java.util.Set;
import java.util.UUID;

public interface FieldManager {
    FieldDTO createFieldDefault(@NotNull FieldDTO fieldDTO);

    FieldDTO createFieldInGroup(@NotNull FieldDTO fieldDTO, @NotNull UUID groupId);

    FieldDTO getFieldById(@NotNull UUID id);

    Set<FieldDTO> getAllFields();

    FieldDTO updateField(@NotNull FieldDTO fieldDTO);

    @Transactional(readOnly = true)
    Field getFieldIfExists(UUID fieldId);

    void deleteFieldWithData(@NotNull UUID fieldId);

    void archiveField(@NotNull UUID fieldId);

    void deArchiveField(@NotNull UUID fieldId);
}
