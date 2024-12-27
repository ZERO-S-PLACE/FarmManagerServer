package org.zeros.farm_manager_server.DAO.Interface;

import org.zeros.farm_manager_server.entities.Fields.Field;
import org.zeros.farm_manager_server.entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.entities.Fields.FieldPart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserFieldsManager {

    FieldGroup createEmptyFieldGroup(String fieldGroupName, String description);

    FieldGroup getFieldGroupByName(String groupName);

    FieldGroup getFieldGroupById(UUID id);

    FieldGroup updateFieldGroupAndDescription(FieldGroup group);

    List<FieldGroup> getAllFieldGroups();

    void deleteFieldGroupWithFields(FieldGroup group);

    void deleteFieldGroupWithoutFields(FieldGroup group);

    void moveFieldsToAnotherGroup(Set<Field> fields, FieldGroup newGroup);

    Field createFieldDefault(Field field);

    Field createFieldInGroup(Field field, FieldGroup group);

    Field getFieldById(UUID id);

    Set<Field> getAllFields();

    Field updateFieldInfo(Field field);

    void deleteFieldWithData(Field field);

    void archiveField(Field field);

    Field deArchiveField(Field field);

    Field divideFieldPart(FieldPart originPart, FieldPart part1, FieldPart part2);

    FieldPart mergeFieldParts(Set<FieldPart> fieldParts);

    Set<FieldPart> getAllFieldParts(Field field);

    Set<FieldPart> getAllNonArchivedFieldParts(Field field);

    FieldPart updateFieldPartName(FieldPart fieldPart, String newName);

    Field updateFieldPartAreaResizeField(FieldPart fieldPart, BigDecimal newArea);

    Field updateFieldPartAreaTransfer(FieldPart changedPart, FieldPart resizedPart, BigDecimal newArea);

    FieldPart getFieldPartById(UUID id);


}
