package org.zeros.farm_manager_server.Services.Default.Fields;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldManager;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class FieldManagerDefault implements FieldManager {

    private final UserRepository userRepository;
    private final FieldRepository fieldRepository;
    private final FieldPartRepository fieldPartRepository;
    private final EntityManager entityManager;
    private final LoggedUserConfiguration loggedUserConfiguration;
    private final FieldGroupManagerDefault fieldGroupManager;


    @Override
    @Transactional
    public FieldDTO createFieldDefault(FieldDTO fieldDTO) {
        return createFieldInGroup(fieldDTO, fieldGroupManager.getOrCreateDefaultFieldGroup().getId());
    }

    @Override
    @Transactional
    public FieldDTO createFieldInGroup(FieldDTO fieldDTO, UUID groupId) {
        FieldGroup fieldGroup = fieldGroupManager.getFieldGroupIfExists(groupId);
        Field field = rewriteToEntity(fieldDTO, Field.NONE);
        field.setFieldName(validateNewFieldName(fieldDTO.getFieldName()));
        field.setFieldGroup(fieldGroup);
        field.setUser(loggedUserConfiguration.getLoggedUser());
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
    @Transactional
    public FieldDTO getFieldById(UUID id) {
        Field field = fieldRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(Field.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return DefaultMappers.fieldMapper.entityToDto(field);
    }

    @Override
    @Transactional
    public Set<FieldDTO> getAllFields() {
        User user = userRepository.findUserById(loggedUserConfiguration.getLoggedUser().getId()).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(User.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return user.getFields().stream().map(DefaultMappers.fieldMapper::entityToDto).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public FieldDTO updateField(FieldDTO fieldDTO) {
        Field originalField = getFieldIfExists(fieldDTO.getId());
        if (!originalField.getFieldName().equals(fieldDTO.getFieldName())) {
            fieldDTO.setFieldName(validateNewFieldName(fieldDTO.getFieldName()));
        }
        Field field = rewriteToEntity(fieldDTO, originalField);
        field.setArea(originalField.getArea());
        Field saved = fieldRepository.saveAndFlush(field);
        return DefaultMappers.fieldMapper.entityToDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Field getFieldIfExists(UUID fieldId) {
        if (fieldId == null) {
            throw new IllegalArgumentExceptionCustom(Field.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return fieldRepository.findById(fieldId).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(Field.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }

    @Override
    @Transactional
    public void deleteFieldWithData(UUID fieldId) {
        Field field = fieldRepository.findById(fieldId).orElse(Field.NONE);
        if (field == Field.NONE) {
            return;
        }
        fieldRepository.delete(field);
    }

    @Override
    @Transactional
    public void archiveField(UUID fieldId) {
        Field field = getFieldIfExists(fieldId);
        field.setIsArchived(true);
        flushChanges();
    }

    @Override
    @Transactional
    public void deArchiveField(UUID fieldId) {
        Field field = getFieldIfExists(fieldId);
        field.setIsArchived(false);
        flushChanges();
    }

    private String validateNewFieldName(String name) {
        User user = userRepository.findById(loggedUserConfiguration.getLoggedUser().getId()).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(User.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
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

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }
}
