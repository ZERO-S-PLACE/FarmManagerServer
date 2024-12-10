package org.zeros.farm_manager_server.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.entities.User;
import org.zeros.farm_manager_server.entities.fields.Field;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;
import org.zeros.farm_manager_server.entities.fields.FieldPart;

import java.util.Set;
import java.util.UUID;

@Component
public class UserFieldsManagerDefault implements UserFieldsManager{

    private final User user;

    public UserFieldsManagerDefault(User user) {
        this.user = user;
    }

    @Override
    public FieldGroup createEmptyFieldGroup(FieldGroup fieldGroup) {
        return null;
    }

    @Override
    public FieldGroup getFieldGroupByName(String groupName) {
        return null;
    }

    @Override
    public FieldGroup getFieldGroupById(UUID id) {
        return null;
    }

    @Override
    public FieldGroup updateFieldGroup(FieldGroup group) {
        return null;
    }

    @Override
    public void deleteFieldGroupWithFields(FieldGroup group) {

    }

    @Override
    public void deleteFieldGroupWithoutFields(FieldGroup group) {

    }

    @Override
    public Field createFieldDefault(Field field) {
        return null;
    }

    @Override
    public Field createFieldInGroup(Field field, FieldGroup group) {
        return null;
    }

    @Override
    public Field getFieldById(UUID id) {
        return null;
    }

    @Override
    public Field getFieldByName(String name) {
        return null;
    }

    @Override
    public Set<Field> getFieldsByGroup(FieldGroup group) {
        return Set.of();
    }

    @Override
    public Set<Field> getAllFields() {
        return Set.of();
    }

    @Override
    public Field updateFieldInfo(Field field) {
        return null;
    }

    @Override
    public Field updateFieldFieldGroup(Field field, FieldGroup group) {
        return null;
    }

    @Override
    public Field deleteFieldWithData(Field field) {
        return null;
    }

    @Override
    public Field deleteFieldMaintainData(Field field) {
        return null;
    }

    @Override
    public void archiveField(Field field) {

    }

    @Override
    public void deArchiveField(Field field) {

    }

    @Override
    public Field divideField(Field field, Set<FieldPart> fieldParts) {
        return null;
    }

    @Override
    public Field mergeFieldParts(Field field, Set<FieldPart> fieldParts) {
        return null;
    }

    @Override
    public Field updateFieldPartName(FieldPart fieldPart, String newName) {
        return null;
    }

    @Override
    public Field updateFieldPartAreaShrinkField(FieldPart fieldPart) {
        return null;
    }

    @Override
    public Field updateFieldPartAreaTransfer(FieldPart fieldPart1, FieldPart fieldPart2) {
        return null;
    }
}
