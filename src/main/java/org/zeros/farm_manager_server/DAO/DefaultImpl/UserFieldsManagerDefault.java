package org.zeros.farm_manager_server.DAO.DefaultImpl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.UserFieldsManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.Field;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;
import org.zeros.farm_manager_server.entities.fields.FieldPart;
import org.zeros.farm_manager_server.repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.*;

@Component
public class UserFieldsManagerDefault implements UserFieldsManager {

    private final UserRepository userRepository;
    private User user;

    private final FieldGroupRepository fieldGroupRepository;
    private final FieldRepository fieldRepository;
    private final FieldPartRepository fieldPartRepository;
    private final EntityManager entityManager;

    public UserFieldsManagerDefault(@Autowired LoggedUserConfiguration loggedUserConfiguration,
                                    @Autowired FieldGroupRepository fieldGroupRepository,
                                    @Autowired FieldRepository fieldRepository,
                                    @Autowired FieldPartRepository fieldPartRepository,@Autowired UserRepository userRepository,@Autowired EntityManager entityManager) {
        this.fieldGroupRepository = fieldGroupRepository;
        this.fieldRepository = fieldRepository;
        this.fieldPartRepository = fieldPartRepository;
        this.user = loggedUserConfiguration.getLoggedUserProperty().get();
        this.entityManager = entityManager;
        loggedUserConfiguration.getLoggedUserProperty().addListener(((observable, oldValue, newValue) -> user=newValue));
        this.userRepository = userRepository;
    }

    @Override
    public FieldGroup createEmptyFieldGroup(String fieldGroupName, String description) {
        if (fieldGroupName.isBlank()) {
            fieldGroupName="NewGroup"+fieldGroupRepository.findAll().size();
        }
        if (fieldGroupRepository.findByUserAndFieldGroupName(user,fieldGroupName).isPresent()) {
            fieldGroupName=fieldGroupName+"_1";
        }
        FieldGroup fieldGroup= FieldGroup.builder().
                fieldGroupName(fieldGroupName)
                .description(description)
                .user(user)
                .build();
        user.addFieldGroup(fieldGroup);
        FieldGroup fieldGroupSaved =fieldGroupRepository.saveAndFlush(fieldGroup);
        flushChanges();
        return fieldGroupRepository.findById(fieldGroupSaved.getId()).orElse(FieldGroup.NONE);
    }

    @Override
    public FieldGroup getFieldGroupByName(String groupName) {
        return fieldGroupRepository.findByUserAndFieldGroupName(user,groupName).orElse(FieldGroup.NONE);
    }

    @Override
    public FieldGroup getFieldGroupById(UUID id) {
        return fieldGroupRepository.findById(id).orElse(FieldGroup.NONE);
    }

    @Override
    public FieldGroup updateFieldGroupAndDescription(FieldGroup group) {
        Optional<FieldGroup> fieldGroupSaved=fieldGroupRepository.findById(group.getId());
        if(fieldGroupSaved.isEmpty()) {
            return createEmptyFieldGroup(group.getFieldGroupName(),group.getDescription());
        }
        FieldGroup fieldGroupUpdated=fieldGroupSaved.get();
        fieldGroupUpdated.setDescription(group.getDescription());
        fieldGroupUpdated.setFieldGroupName(group.getFieldGroupName());
        return fieldGroupRepository.saveAndFlush(fieldGroupUpdated);
    }

    @Override
    public List<FieldGroup> getAllFieldGroups() {
        return fieldGroupRepository.findAllByUser(user);
    }

    @Override
    public void deleteFieldGroupWithFields(FieldGroup group) {
        fieldGroupRepository.delete(group);
    }

    @Override
    @Transactional
    public void deleteFieldGroupWithoutFields(FieldGroup group) {
        moveFieldsToAnotherGroup(group.getFields(), getOrCreateDefaultFieldGroup());
        fieldGroupRepository.delete(group);
        flushChanges();
    }

    private @NonNull FieldGroup getOrCreateDefaultFieldGroup() {
        FieldGroup defaultGroup;
        Optional<FieldGroup> optionalDefaultGroup=
                fieldGroupRepository.findByUserAndFieldGroupName(user,"DEFAULT");
        if(optionalDefaultGroup.isEmpty()){
            defaultGroup=FieldGroup.getDefaultFieldGroup(user);
            user.addFieldGroup(defaultGroup);
            fieldGroupRepository.saveAndFlush(defaultGroup);
        }else {
            defaultGroup=optionalDefaultGroup.get();
        }
        return defaultGroup;
    }

    @Override
    public void moveFieldsToAnotherGroup(Set<Field> fields, FieldGroup newGroup) {
        for (Field field : fields) {
            field=fieldRepository.findById(field.getId()).orElse(Field.NONE);
            if(!field.equals(Field.NONE)) {
                field.setFieldGroup(newGroup);
                newGroup.addField(field);
            }
        }
        flushChanges();
    }

    @Override
    public Field createFieldDefault(Field field) {
       return createFieldInGroup(field,getOrCreateDefaultFieldGroup());
    }

    @Override
    public Field createFieldInGroup(Field field, FieldGroup group) {
        validateFieldName(field);
        field.setFieldGroup(group);
        group.addField(field);
        field.setUser(user);
        Field fieldSaved=fieldRepository.saveAndFlush(field);
        FieldPart defaultPart=FieldPart.getDefaultFieldPart(fieldSaved);
        field.setFieldParts(Set.of(defaultPart));
        fieldPartRepository.saveAndFlush(defaultPart);

        return fieldSaved ;
    }



    @Override
    public Field getFieldById(UUID id) {
        return fieldRepository.findById(id).orElse(Field.NONE);
    }

    @Override
    public Set<Field> getAllFields() {
        return userRepository.findUserById(user.getId()).orElse(User.NONE).getFields();
    }

    @Override
    public Field updateFieldInfo(Field field) {
        Field currentField=fieldRepository.findById(field.getId()).orElse(Field.NONE);

        if(currentField.equals(Field.NONE))return Field.NONE;
        field.setFieldParts(currentField.getFieldParts());

        if(field.getFieldName().isBlank())field.setFieldName(currentField.getFieldName());
        else {validateFieldName(field);}

        if((!(field.getArea().floatValue()==currentField.getArea().floatValue()))&&field.getArea().floatValue()>=0){
            FieldPart fieldPart=currentField.getFieldParts().stream().findFirst().orElse(FieldPart.NONE);
            if(fieldPart.equals(FieldPart.NONE))return Field.NONE;
            if(currentField.getFieldParts().size()==1){
                fieldPart.setArea(field.getArea());
            }else {
                return Field.NONE;
            }
            fieldPartRepository.saveAndFlush(fieldPart);
        }else field.setArea(currentField.getArea());

        return fieldRepository.saveAndFlush(field);
    }



    @Override
    public void deleteFieldWithData(Field field) {
        fieldRepository.delete(field);
    }

    @Override
    public void archiveField(Field field) {
        field=fieldRepository.findById(field.getId()).orElse(Field.NONE);
        field.setIsArchived(true);
        flushChanges();
    }

    @Override
    public Field deArchiveField(Field field) {
        field=fieldRepository.findById(field.getId()).orElse(Field.NONE);
        field.setIsArchived(true);
        flushChanges();
        return field;
    }

    @Override
    public Field divideFieldPart(FieldPart originPart, FieldPart part1, FieldPart part2) {
        originPart=fieldPartRepository.findById(originPart.getId()).orElse(FieldPart.NONE);
        if(originPart.equals(FieldPart.NONE))return Field.NONE;
        if(!(originPart.getArea().floatValue()==part1.getArea().floatValue()+part2.getArea().floatValue())) {
        if(part1.getArea().floatValue()>originPart.getArea().floatValue()) {
            return Field.NONE;
        }
        part2.setArea(originPart.getArea().subtract(part1.getArea()));
        }
        part1.setField(originPart.getField());
        part2.setField(originPart.getField());
        originPart.setIsArchived(true);
        fieldPartRepository.saveAndFlush(part1);
        fieldPartRepository.saveAndFlush(part2);
        flushChanges();
        return fieldRepository.findById(originPart.getField().getId()).orElse(Field.NONE);
    }

    @Override
    public FieldPart mergeFieldParts(Set<FieldPart> fieldParts) {
        if(fieldParts.isEmpty())return FieldPart.NONE;
        FieldPart firstPart=fieldParts.stream().findFirst().orElse(FieldPart.NONE);
        fieldParts.remove(firstPart);
        FieldPart merged=FieldPart.builder().fieldPartName(firstPart.getFieldPartName()).area(firstPart.getArea()).field(firstPart.getField()).build();
        BigDecimal targetArea=merged.getArea();
        for(FieldPart fieldPart:fieldParts) {
           targetArea=targetArea.add(fieldPart.getArea());
           fieldPart.setIsArchived(true);
        }
        merged.setFieldPartName(merged.getFieldPartName()+"_merged");
        validateFieldPartName(merged);
        merged.setArea(targetArea);
        merged.setId(null);
        merged.setIsArchived(false);
        firstPart.setIsArchived(true);
        FieldPart mergedSaved=fieldPartRepository.saveAndFlush(merged);
        flushChanges();
        return mergedSaved;
    }

    @Override
    public Set<FieldPart> getAllFieldParts(Field field) {
        return fieldRepository.findById(field.getId()).orElse(Field.NONE).getFieldParts();
    }

    @Override
    public Set<FieldPart> getAllNonArchivedFieldParts(Field field) {
        return new HashSet<>(fieldPartRepository.findAllByFieldAndIsArchived(field, false));
    }

    @Override
    public FieldPart updateFieldPartName(FieldPart fieldPart, String newName) {
        fieldPart=fieldPartRepository.findById(fieldPart.getId()).orElse(FieldPart.NONE);
        fieldPart.setFieldPartName(newName);
        validateFieldPartName(fieldPart);
        flushChanges();
        return fieldPart;
    }

    @Override
    public Field updateFieldPartAreaResizeField(FieldPart fieldPart, BigDecimal newArea) {
        fieldPart=fieldPartRepository.findById(fieldPart.getId()).orElse(FieldPart.NONE);
        Field field=fieldRepository.findById(fieldPart.getField().getId()).orElse(Field.NONE);
        if(fieldPart.equals(FieldPart.NONE)||field.equals(Field.NONE))return Field.NONE;
        field.setArea(field.getArea().subtract(fieldPart.getArea()).add(newArea));
        fieldPart.setArea(newArea);
        flushChanges();
        return field;
    }

    @Override
    public Field updateFieldPartAreaTransfer(FieldPart basePart, FieldPart resizedPart, BigDecimal newArea) {
        basePart =fieldPartRepository.findById(basePart.getId()).orElse(FieldPart.NONE);
        resizedPart =fieldPartRepository.findById(resizedPart.getId()).orElse(FieldPart.NONE);
        Field field=fieldRepository.findById(basePart.getField().getId()).orElse(Field.NONE);
        if(basePart.equals(FieldPart.NONE)||field.equals(Field.NONE)|| resizedPart.equals(FieldPart.NONE))return Field.NONE;
        resizedPart.setArea(resizedPart.getArea().subtract(basePart.getArea()).add(newArea));
        basePart.setArea(newArea);
        flushChanges();
        return field;
    }

    @Override
    public FieldPart getFieldPartById(UUID id) {
        return fieldPartRepository.findById(id).orElse(FieldPart.NONE);
    }


    private void validateFieldName(Field field) {
        if(field.getFieldName().isBlank()){
            field.setFieldName("NewField"+fieldRepository.findAllByUser(user).size());
        }
        for(Field field1: user.getFields())
        {
            if(field1.getFieldName().equals(field.getFieldName()))
            {
                if(!field1.getId().equals(field.getId())) {
                    field.setFieldName(field.getFieldName() + "_1");
                    break;
                }
            }
        }
    }
    private void validateFieldPartName(FieldPart fieldPart) {
        if(fieldPart.getFieldPartName().isBlank()){
            fieldPart.setFieldPartName("NewPart"+fieldPartRepository.findAllByField(fieldPart.getField()).size());
        }
        for(FieldPart fieldPart1: fieldPart.getField().getFieldParts())
        {
            if(fieldPart1.getFieldPartName().equals(fieldPart.getFieldPartName()))
                if(!fieldPart1.getId().equals(fieldPart.getId())) {
                    fieldPart.setFieldPartName(fieldPart.getFieldPartName()+ "_1");
                    break;
                }
            }
        }


    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }
}
