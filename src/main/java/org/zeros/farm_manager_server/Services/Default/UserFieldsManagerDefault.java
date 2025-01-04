package org.zeros.farm_manager_server.Services.Default;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.CustomException.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.CustomException.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldGroupDTO;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldPartDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Repositories.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.UserFieldsManager;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class UserFieldsManagerDefault implements UserFieldsManager {

    private final UserRepository userRepository;
    private final FieldGroupRepository fieldGroupRepository;
    private final FieldRepository fieldRepository;
    private final FieldPartRepository fieldPartRepository;
    private final EntityManager entityManager;
    private final LoggedUserConfiguration loggedUserConfiguration;
    private final UserManagerDefault userManagerDefault;

    @Override
    public FieldGroup createEmptyFieldGroup(String fieldGroupName, String description) {
        User user = loggedUserConfiguration.getLoggedUserProperty().get();
        fieldGroupName = validateFieldGroupName(fieldGroupName);
        FieldGroup fieldGroup = FieldGroup.builder().
                fieldGroupName(fieldGroupName)
                .description(description)
                .user(user)
                .build();
        FieldGroup fieldGroupSaved = fieldGroupRepository.saveAndFlush(fieldGroup);
        user.addFieldGroup(fieldGroup);
        flushChanges();
        return fieldGroupRepository.findById(fieldGroupSaved.getId()).orElse(FieldGroup.NONE);
    }

    private String validateFieldGroupName(String fieldGroupName) {
        if (fieldGroupName.isBlank()) {
            fieldGroupName = "NewGroup" + fieldGroupRepository.findAll().size();
        }
        if (fieldGroupRepository.findByUserAndFieldGroupName(loggedUserConfiguration.getLoggedUserProperty().get(), fieldGroupName).isPresent()) {
            fieldGroupName = fieldGroupName + "_1";
        }
        return fieldGroupName;
    }

    @Override
    public FieldGroup getFieldGroupByName(String groupName) {
        return fieldGroupRepository.findByUserAndFieldGroupName(loggedUserConfiguration.getLoggedUserProperty().get(), groupName).orElse(FieldGroup.NONE);
    }

    @Override
    public FieldGroup getFieldGroupById(UUID id) {
        return fieldGroupRepository.findById(id).orElse(FieldGroup.NONE);
    }

    @Override
    public FieldGroup updateFieldGroup(FieldGroupDTO groupDTO) {
        FieldGroup originalFieldGroup = getFieldGroupIfExists(groupDTO.getId());
        originalFieldGroup.setDescription(groupDTO.getDescription());
        originalFieldGroup.setFieldGroupName(groupDTO.getFieldGroupName());
        return fieldGroupRepository.saveAndFlush(originalFieldGroup);
    }

    private FieldGroup getFieldGroupIfExists(UUID groupId) {
        if (groupId == null) {
            throw new IllegalArgumentExceptionCustom(FieldGroup.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        FieldGroup originalFieldGroup = getFieldGroupById(groupId);
        if (originalFieldGroup.equals(FieldGroup.NONE)) {
            throw new IllegalArgumentExceptionCustom(FieldGroup.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return originalFieldGroup;
    }

    @Override
    public Set<FieldGroup> getAllFieldGroups() {
        return fieldGroupRepository.findAllByUser(loggedUserConfiguration.getLoggedUserProperty().get());
    }

    @Override
    public void deleteFieldGroupWithFields(UUID groupId) {
        FieldGroup fieldGroup = getFieldGroupById(groupId);
        if (fieldGroup == FieldGroup.NONE) {
            return;
        }
        fieldGroup.getFields().forEach(field -> deleteFieldWithData(field.getId()));
        fieldGroupRepository.delete(fieldGroup);
        flushChanges();
    }

    @Override
    public void deleteFieldGroupWithoutFields(UUID groupId) {
        FieldGroup fieldGroup = getFieldGroupById(groupId);
        if (fieldGroup == FieldGroup.NONE) {
            return;
        }
        if (fieldGroup.getFieldGroupName().equals("DEFAULT")) {
            throw new IllegalAccessErrorCustom(FieldGroup.class, IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
        }
        moveFieldsToAnotherGroup(fieldGroup.getFields(), getOrCreateDefaultFieldGroup());
        fieldGroupRepository.delete(fieldGroup);
        flushChanges();
    }

    private FieldGroup getOrCreateDefaultFieldGroup() {
        User user = loggedUserConfiguration.getLoggedUserProperty().get();
        FieldGroup defaultGroup;
        Optional<FieldGroup> optionalDefaultGroup =
                fieldGroupRepository.findByUserAndFieldGroupName(user, "DEFAULT");
        if (optionalDefaultGroup.isEmpty()) {
            defaultGroup = FieldGroup.getDefaultFieldGroup(user);
            user.addFieldGroup(defaultGroup);
            fieldGroupRepository.saveAndFlush(defaultGroup);
        } else {
            defaultGroup = optionalDefaultGroup.get();
        }
        return defaultGroup;
    }

    @Override
    public void moveFieldsToAnotherGroup(Set<UUID> fieldsIds, UUID newGroupId) {
        Set<Field> fields = fieldsIds.stream().map(this::getFieldById).collect(Collectors.toSet());
        if (fields.contains(Field.NONE)) {
            throw new IllegalArgumentExceptionCustom(Field.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        FieldGroup newGroup = getFieldGroupById(newGroupId);
        if (newGroup == FieldGroup.NONE) {
            newGroup = getOrCreateDefaultFieldGroup();
        }
        moveFieldsToAnotherGroup(fields, newGroup);

    }

    private void moveFieldsToAnotherGroup(Set<Field> fields, FieldGroup newGroup) {
        for (Field field : fields) {
            field = fieldRepository.findById(field.getId()).orElse(Field.NONE);
            if (!field.equals(Field.NONE)) {
                field.setFieldGroup(newGroup);
                newGroup.addField(field);
                flushChanges();
            }
        }
    }

    @Override
    public Field createFieldDefault(FieldDTO fieldDTO) {
        return createFieldInGroup(fieldDTO, getOrCreateDefaultFieldGroup().getId());
    }

    @Override
    public Field createFieldInGroup(FieldDTO fieldDTO, UUID groupId) {
        FieldGroup fieldGroup = getFieldGroupIfExists(groupId);
        Field field = rewriteToEntity(fieldDTO, Field.NONE);
        field.setFieldName(validateNewFieldName(fieldDTO.getFieldName()));
        field.setFieldGroup(fieldGroup);
        field.setUser(loggedUserConfiguration.getLoggedUserProperty().get());
        Field fieldSaved = fieldRepository.saveAndFlush(field);
        FieldPart defaultPart = FieldPart.getDefaultFieldPart(fieldSaved);
        fieldSaved.setFieldParts(Set.of(defaultPart));
        fieldPartRepository.saveAndFlush(defaultPart);
        flushChanges();
        return getFieldById(fieldSaved.getId());
    }

    private Field rewriteToEntity(FieldDTO dto, Field entity) {
        Field parsedEntity = DefaultMappers.fieldMapper.dtoToEntitySimpleProperties(dto);
        parsedEntity.setUser(entity.getUser());
        parsedEntity.setFieldGroup(entity.getFieldGroup());
        parsedEntity.setFieldParts(entity.getFieldParts());
        parsedEntity.setVersion(entity.getVersion());
        parsedEntity.setLastModifiedDate(entity.getLastModifiedDate());
        parsedEntity.setCreatedDate(entity.getCreatedDate());
        return parsedEntity;
    }


    @Override
    public Field getFieldById(UUID id) {
        return fieldRepository.findById(id).orElse(Field.NONE);
    }

    @Override
    public Set<Field> getAllFields() {
        return userRepository.findUserById(loggedUserConfiguration.getLoggedUserProperty().get().getId())
                .orElse(User.NONE).getFields();
    }

    @Override
    public Field updateField(FieldDTO fieldDTO) {
        Field originalField = getFieldIfExists(fieldDTO.getId());
        if (!originalField.getFieldName().equals(fieldDTO.getFieldName())) {
            fieldDTO.setFieldName(validateNewFieldName(fieldDTO.getFieldName()));
        }
        Field field = rewriteToEntity(fieldDTO, originalField);
        field.setArea(originalField.getArea());

        return fieldRepository.saveAndFlush(field);
    }

    private Field getFieldIfExists(UUID fieldId) {
        if (fieldId == null) {
            throw new IllegalArgumentExceptionCustom(Field.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        Field originalField = getFieldById(fieldId);
        if (originalField.equals(Field.NONE)) {
            throw new IllegalArgumentExceptionCustom(Field.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return originalField;
    }

    @Override
    public void deleteFieldWithData(UUID fieldId) {
        Field field = getFieldById(fieldId);
        if (field == Field.NONE) {
            return;
        }
        fieldRepository.delete(field);
    }

    @Override
    public void archiveField(UUID fieldId) {
        Field field = getFieldById(fieldId);
        field.setIsArchived(true);
        flushChanges();
    }

    @Override
    public void deArchiveField(UUID fieldId) {
        Field field = getFieldById(fieldId);
        field.setIsArchived(false);
        flushChanges();
    }

    @Override
    public Field divideFieldPart(UUID originPartId, FieldPartDTO part1DTO, FieldPartDTO part2DTO) {
        FieldPart originPart = fieldPartRepository.findById(originPartId).orElse(FieldPart.NONE);
        if (originPart.equals(FieldPart.NONE)) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        if (part1DTO.getArea() >= originPart.getArea().floatValue()) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.NOT_COMPATIBLE);
        }
        Field field = originPart.getField();
        FieldPart part1 = rewriteToEntity(part1DTO, FieldPart.NONE);
        FieldPart part2 = rewriteToEntity(part1DTO, FieldPart.NONE);
        part2.setArea(originPart.getArea().subtract(part1.getArea()));
        part1.setField(field);
        part2.setField(field);
        originPart.setIsArchived(true);
        FieldPart part1Saved = fieldPartRepository.saveAndFlush(part1);
        FieldPart part2Saved = fieldPartRepository.saveAndFlush(part2);
        field.getFieldParts().add(part1Saved);
        field.getFieldParts().add(part2Saved);
        flushChanges();
        return getFieldById(field.getId());
    }

    private FieldPart rewriteToEntity(FieldPartDTO dto, FieldPart entity) {
        FieldPart parsedEntity = DefaultMappers.fieldPartMapper.dtoToEntitySimpleProperties(dto);
        parsedEntity.setField(entity.getField());
        parsedEntity.setCrops(entity.getCrops());
        parsedEntity.setCreatedDate(entity.getCreatedDate());
        parsedEntity.setLastModifiedDate(entity.getLastModifiedDate());
        parsedEntity.setCreatedDate(entity.getCreatedDate());
        return parsedEntity;
    }

    @Override
    public FieldPart mergeFieldParts(Set<UUID> fieldPartsIds) {
        Set<FieldPart> fieldParts = fieldPartsIds.stream().map(this::getFieldPartById).collect(Collectors.toSet());
        if (fieldParts.size() < 2) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.NOT_COMPATIBLE);
        }
        if (fieldParts.contains(FieldPart.NONE)) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }

        FieldPart firstPart = fieldParts.stream().findFirst().orElse(FieldPart.NONE);
        fieldParts.remove(firstPart);
        FieldPart merged = FieldPart.builder().fieldPartName(firstPart.getFieldPartName()).area(firstPart.getArea()).field(firstPart.getField()).build();
        BigDecimal targetArea = merged.getArea();
        for (FieldPart fieldPart : fieldParts) {
            targetArea = targetArea.add(fieldPart.getArea());
            fieldPart.setIsArchived(true);
        }
        merged.setFieldPartName(validateNewFieldPartName(merged.getFieldPartName() + "_merged", firstPart.getField().getId()));
        merged.setArea(targetArea);
        merged.setId(null);
        merged.setIsArchived(false);
        firstPart.setIsArchived(true);
        FieldPart mergedSaved = fieldPartRepository.saveAndFlush(merged);
        flushChanges();
        return mergedSaved;
    }

    @Override
    public Set<FieldPart> getAllFieldParts(UUID fieldId) {
        return getFieldById(fieldId).getFieldParts();
    }

    @Override
    public Set<FieldPart> getAllNonArchivedFieldParts(UUID fieldId) {
        return new HashSet<>(fieldPartRepository.findAllByFieldAndIsArchived(getFieldById(fieldId), false));
    }

    @Override
    public FieldPart updateFieldPartName(UUID fieldPartId, String newName) {
        FieldPart fieldPart = getFieldPartIfExist(fieldPartId);
        if (newName.equals(fieldPart.getFieldPartName())) {
            return fieldPart;
        }
        fieldPart.setFieldPartName(validateNewFieldPartName(newName, fieldPart.getField().getId()));
        flushChanges();
        return getFieldPartById(fieldPartId);
    }

    private FieldPart getFieldPartIfExist(UUID fieldPartId) {
        if (fieldPartId == null) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        FieldPart fieldPart = getFieldPartById(fieldPartId);
        if (fieldPart.equals(FieldPart.NONE)) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return fieldPart;
    }

    @Override
    public Field updateFieldPartAreaResizeField(UUID fieldPartId, BigDecimal newArea) {
        FieldPart fieldPart = getFieldPartIfExist(fieldPartId);
        Field field = fieldPart.getField();
        field.setArea(field.getArea().subtract(fieldPart.getArea()).add(newArea));
        fieldPart.setArea(newArea);
        flushChanges();
        return field;
    }

    @Override
    public Field updateFieldPartAreaTransfer(UUID basePartId, UUID resizedPartId, BigDecimal newArea) {
        FieldPart basePart = getFieldPartIfExist(basePartId);
        FieldPart resizedPart = getFieldPartIfExist(resizedPartId);
        Field field = fieldRepository.findById(basePart.getField().getId()).orElse(Field.NONE);
        if (basePart.equals(FieldPart.NONE) || field.equals(Field.NONE) || resizedPart.equals(FieldPart.NONE))
            return Field.NONE;
        resizedPart.setArea(resizedPart.getArea().subtract(basePart.getArea()).add(newArea));
        basePart.setArea(newArea);
        flushChanges();
        return field;
    }

    @Override
    public FieldPart getFieldPartById(UUID id) {
        return fieldPartRepository.findById(id).orElse(FieldPart.NONE);
    }


    private String validateNewFieldName(String name) {
        User user = userManagerDefault.getUserById(loggedUserConfiguration.getLoggedUserProperty().get().getId());
        if (name.isBlank()) {
            name = "NewField" + fieldRepository.findAllByUser(user).size();
        }
        boolean nameUpdated;
        do {
            nameUpdated = false;
            for (Field field : user.getFields()) {
                if (field.getFieldName().equals(name)) {
                    name = name + "_1";
                    nameUpdated = true;
                }
            }
        } while (nameUpdated);
        return name;
    }

    private String validateNewFieldPartName(String name, UUID fieldId) {
        Field field = getFieldById(fieldId);
        if (field.equals(Field.NONE)) {
            throw new IllegalArgumentExceptionCustom(
                    FieldPart.class, Set.of("field"), IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        if (name.isBlank()) {
            name = "NewPart" + fieldPartRepository.findAllByField(field).size();
        }
        boolean nameUpdated;
        do {
            nameUpdated = false;
            for (FieldPart fieldPart : field.getFieldParts()) {
                if (fieldPart.getFieldPartName().equals(name)) {
                    name = name + "_1";
                    nameUpdated = true;
                }
            }
        } while (nameUpdated);
        return name;
    }


    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }
}
