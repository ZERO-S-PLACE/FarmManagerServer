package org.zeros.farm_manager_server.DAO;

import org.zeros.farm_manager_server.entities.fields.Field;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;
import org.zeros.farm_manager_server.entities.fields.FieldPart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserFieldsManager {

    public abstract FieldGroup createEmptyFieldGroup(String fieldGroupName, String description);
    public abstract FieldGroup getFieldGroupByName(String groupName);
    public abstract FieldGroup getFieldGroupById(UUID id);
    public abstract FieldGroup updateFieldGroupAndDescription(FieldGroup group);
    public abstract List<FieldGroup> getAllFieldGroups();
    public abstract void deleteFieldGroupWithFields(FieldGroup group);
    public abstract void deleteFieldGroupWithoutFields(FieldGroup group);
    public abstract void moveFieldsToAnotherGroup(Set<Field> fields,FieldGroup newGroup);
    public abstract Field createFieldDefault(Field field);
    public abstract Field createFieldInGroup(Field field, FieldGroup group);
    public abstract Field getFieldById(UUID id);
    public abstract Set<Field> getAllFields();
    public abstract Field updateFieldInfo(Field field);
    public abstract void deleteFieldWithData(Field field);
    public abstract Field archiveField(Field field);
    public abstract Field deArchiveField(Field field);
    public abstract Field divideFieldPart(FieldPart originPart,FieldPart part1,FieldPart part2);
    public abstract FieldPart mergeFieldParts(Set<FieldPart> fieldParts);
    public abstract Set<FieldPart> getAllFieldParts(Field field);
    public abstract Set<FieldPart> getAllNonArchivedFieldParts(Field field);
    public abstract FieldPart updateFieldPartName(FieldPart fieldPart, String newName);
    public abstract Field updateFieldPartAreaResizeField(FieldPart fieldPart, BigDecimal newArea);
    public abstract Field updateFieldPartAreaTransfer(FieldPart changedPart, FieldPart resizedPart,BigDecimal newArea);



}
