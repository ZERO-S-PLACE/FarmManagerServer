package org.zeros.farm_manager_server.Services.Default.Fields;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldGroupDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldGroupManager;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class FieldGroupManagerDefault implements FieldGroupManager {

    private final FieldGroupRepository fieldGroupRepository;
    private final FieldRepository fieldRepository;
    private final EntityManager entityManager;
    private final LoggedUserConfiguration loggedUserConfiguration;

    @Override
    @Transactional
    public FieldGroupDTO createEmptyFieldGroup(String fieldGroupName, String description) {
        User user = loggedUserConfiguration.getLoggedUser();
        fieldGroupName = validateFieldGroupName(fieldGroupName);
        FieldGroup fieldGroup = FieldGroup.builder().
                fieldGroupName(fieldGroupName)
                .description(description)
                .user(user)
                .build();
        FieldGroup fieldGroupSaved = fieldGroupRepository.saveAndFlush(fieldGroup);
        user.addFieldGroup(fieldGroup);
        flushChanges();
        return getFieldGroupById(fieldGroupSaved.getId());
    }

    private String validateFieldGroupName(String fieldGroupName) {
        if (fieldGroupName.isBlank()) {
            fieldGroupName = "NewGroup" + fieldGroupRepository.findAll().size();
        }
        if (fieldGroupRepository.findByUserAndFieldGroupName(loggedUserConfiguration.getLoggedUser(), fieldGroupName).isPresent()) {
            fieldGroupName = fieldGroupName + "_1";
        }
        return fieldGroupName;
    }

    @Override
    @Transactional(readOnly = true)
    public FieldGroupDTO getFieldGroupByName(String groupName) {
        FieldGroup fieldGroup = fieldGroupRepository.findByUserAndFieldGroupName(loggedUserConfiguration.getLoggedUser(), groupName).orElse(FieldGroup.NONE);
        return DefaultMappers.fieldGroupMapper.entityToDto(fieldGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public FieldGroupDTO getFieldGroupById(UUID id) {
        FieldGroup fieldGroup = fieldGroupRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(FieldGroup.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return DefaultMappers.fieldGroupMapper.entityToDto(fieldGroup);
    }

    @Override
    @Transactional
    public FieldGroupDTO updateFieldGroup(FieldGroupDTO groupDTO) {
        FieldGroup originalFieldGroup = getFieldGroupIfExists(groupDTO.getId());
        originalFieldGroup.setDescription(groupDTO.getDescription());
        originalFieldGroup.setFieldGroupName(groupDTO.getFieldGroupName());
        FieldGroup saved = fieldGroupRepository.saveAndFlush(originalFieldGroup);
        return DefaultMappers.fieldGroupMapper.entityToDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public FieldGroup getFieldGroupIfExists(UUID groupId) {
        if (groupId == null) {
            throw new IllegalArgumentExceptionCustom(FieldGroup.class, Set.of("id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        return fieldGroupRepository.findById(groupId).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(FieldGroup.class,
                        IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<FieldGroupDTO> getAllFieldGroups() {
        return fieldGroupRepository.findAllByUser(loggedUserConfiguration.getLoggedUser())
                .stream().map(DefaultMappers.fieldGroupMapper::entityToDto).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void deleteFieldGroupWithFields(UUID groupId) {
        FieldGroup fieldGroup = fieldGroupRepository.findById(groupId).orElse(FieldGroup.NONE);
        if (fieldGroup == FieldGroup.NONE) {
            return;
        }
        fieldGroup.getFields().forEach(field -> fieldRepository.deleteById(field.getId()));
        fieldGroupRepository.delete(fieldGroup);
        flushChanges();
    }

    @Override
    @Transactional
    public void deleteFieldGroupWithoutFields(UUID groupId) {
        FieldGroup fieldGroup = fieldGroupRepository.findById(groupId).orElse(FieldGroup.NONE);
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

    public FieldGroup getOrCreateDefaultFieldGroup() {
        User user = loggedUserConfiguration.getLoggedUser();
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
    @Transactional
    public void moveFieldsToAnotherGroup(Set<UUID> fieldsIds, UUID newGroupId) {
        Set<Field> fields = fieldsIds.stream().map(id -> fieldRepository.findById(id).orElse(Field.NONE)).collect(Collectors.toSet());
        if (fields.contains(Field.NONE)) {
            throw new IllegalArgumentExceptionCustom(Field.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        FieldGroup newGroup = fieldGroupRepository.findById(newGroupId).orElse(FieldGroup.NONE);
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

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }

}
