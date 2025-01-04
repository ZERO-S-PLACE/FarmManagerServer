package org.zeros.farm_manager_server.Services.Interface;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldGroupDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldPartDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public interface UserFieldsManager {

    FieldGroup createEmptyFieldGroup(@NotNull String fieldGroupName,@NotNull String description);

    FieldGroup getFieldGroupByName(@NotNull String groupName);

    FieldGroup getFieldGroupById(@NotNull UUID id);

    FieldGroup updateFieldGroup(@NotNull FieldGroupDTO groupDTO);

    Set<FieldGroup> getAllFieldGroups();

    void deleteFieldGroupWithFields(@NotNull UUID groupId);

    void deleteFieldGroupWithoutFields(@NotNull UUID groupId);

    void moveFieldsToAnotherGroup(@NotNull Set<UUID> fieldsIds, @NotNull UUID newGroupId);

    Field createFieldDefault(@NotNull FieldDTO fieldDTO);

    Field createFieldInGroup(@NotNull FieldDTO fieldDTO,@NotNull UUID groupId);

    Field getFieldById(@NotNull UUID id);

    Set<Field> getAllFields();

    Field updateField(@NotNull FieldDTO fieldDTO);

    void deleteFieldWithData(@NotNull UUID fieldId);

    void archiveField(@NotNull UUID fieldId);

    void deArchiveField(@NotNull UUID fieldId);

    Field divideFieldPart(@NotNull UUID originPartId, @NotNull FieldPartDTO part1DTO, @NotNull FieldPartDTO part2DTO);

    FieldPart mergeFieldParts(@NotNull Set<UUID> fieldPartsIds);

    Set<FieldPart> getAllFieldParts(@NotNull UUID fieldId);

    Set<FieldPart> getAllNonArchivedFieldParts(@NotNull UUID fieldId);

    FieldPart updateFieldPartName(@NotNull UUID fieldPartId,@NotNull String newName);

    Field updateFieldPartAreaResizeField(@NotNull UUID fieldPartId,@NotNull BigDecimal newArea);

    Field updateFieldPartAreaTransfer(@NotNull UUID changedPartId, @NotNull UUID resizedPartId, @NotNull BigDecimal newArea);

    FieldPart getFieldPartById(@NotNull UUID id);


}
