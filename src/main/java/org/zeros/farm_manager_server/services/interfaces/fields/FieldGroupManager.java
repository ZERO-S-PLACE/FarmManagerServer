package org.zeros.farm_manager_server.services.interfaces.fields;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.domain.dto.fields.FieldGroupDTO;
import org.zeros.farm_manager_server.domain.entities.fields.FieldGroup;

import java.util.Set;
import java.util.UUID;

public interface FieldGroupManager {
    FieldGroupDTO createEmptyFieldGroup(@NotNull String fieldGroupName, @NotNull String description);

    FieldGroupDTO getFieldGroupByName(@NotNull String groupName);

    FieldGroupDTO getFieldGroupById(@NotNull UUID id);

    FieldGroupDTO updateFieldGroup(@NotNull FieldGroupDTO groupDTO);

    Set<FieldGroupDTO> getAllFieldGroups();

    void deleteFieldGroupWithFields(@NotNull UUID groupId);

    void deleteFieldGroupWithoutFields(@NotNull UUID groupId);

    void moveFieldsToAnotherGroup(@NotNull Set<UUID> fieldsIds, @NotNull UUID newGroupId);

    FieldGroup getFieldGroupIfExists(UUID groupId);
}
