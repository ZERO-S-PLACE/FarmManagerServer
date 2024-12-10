package org.zeros.farm_manager_server.DAO;

import org.zeros.farm_manager_server.entities.User;
import org.zeros.farm_manager_server.entities.fields.Field;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;
import org.zeros.farm_manager_server.entities.fields.FieldPart;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public interface UserFieldsManager {
    public abstract FieldGroup createEmptyFieldGroup(FieldGroup fieldGroup);
    public abstract FieldGroup getFieldGroupByName(String groupName);
    public abstract FieldGroup getFieldGroupById(UUID id);
    public abstract FieldGroup updateFieldGroup(FieldGroup group);
    public abstract void deleteFieldGroupWithFields(FieldGroup group);
    public abstract void deleteFieldGroupWithoutFields(FieldGroup group);
    public abstract Field createFieldDefault(Field field);
    public abstract Field createFieldInGroup(Field field, FieldGroup group);
    public abstract Field getFieldById(UUID id);
    public abstract Field getFieldByName(String name);
    public abstract Set<Field> getFieldsByGroup(FieldGroup group);
    public abstract Set<Field> getAllFields();
    public abstract Field updateFieldInfo(Field field);
    public abstract Field updateFieldFieldGroup(Field field,FieldGroup group);
    public abstract Field deleteFieldWithData(Field field);
    public abstract Field deleteFieldMaintainData(Field field);
    public abstract void archiveField(Field field);
    public abstract void deArchiveField(Field field);
    public abstract Field divideField(Field field, Set<FieldPart> fieldParts);
    public abstract Field mergeFieldParts(Field field, Set<FieldPart> fieldParts);
    public abstract Field updateFieldPartName(FieldPart fieldPart,String newName);
    public abstract Field updateFieldPartAreaShrinkField(FieldPart fieldPart);
    public abstract Field updateFieldPartAreaTransfer(FieldPart fieldPart1, FieldPart fieldPart2);




}
