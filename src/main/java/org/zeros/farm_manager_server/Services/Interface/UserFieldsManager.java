package org.zeros.farm_manager_server.Services.Interface;

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

    FieldGroup createEmptyFieldGroup(String fieldGroupName, String description);

    FieldGroup getFieldGroupByName(String groupName);

    FieldGroup getFieldGroupById(UUID id);

    FieldGroup updateFieldGroup(FieldGroupDTO groupDTO);

    Set<FieldGroup> getAllFieldGroups();

    void deleteFieldGroupWithFields(UUID groupId);

    void deleteFieldGroupWithoutFields(UUID groupId);

    void moveFieldsToAnotherGroup(Set<UUID> fieldsIds, UUID newGroupId);

    Field createFieldDefault(FieldDTO fieldDTO);

    Field createFieldInGroup(FieldDTO fieldDTO, UUID groupId);

    Field getFieldById(UUID id);

    Set<Field> getAllFields();

    Field updateField(FieldDTO fieldDTO);

    void deleteFieldWithData(UUID fieldId);

    void archiveField(UUID fieldId);

    void deArchiveField(UUID fieldId);

    Field divideFieldPart(UUID originPartId, FieldPartDTO part1DTO, FieldPartDTO part2DTO);

    FieldPart mergeFieldParts(Set<UUID> fieldPartsIds);

    Set<FieldPart> getAllFieldParts(UUID fieldId);

    Set<FieldPart> getAllNonArchivedFieldParts(UUID fieldId);

    FieldPart updateFieldPartName(UUID fieldPartId, String newName);

    Field updateFieldPartAreaResizeField(UUID fieldPartId, BigDecimal newArea);

    Field updateFieldPartAreaTransfer(UUID changedPartId, UUID resizedPartId, BigDecimal newArea);

    FieldPart getFieldPartById(UUID id);


}
