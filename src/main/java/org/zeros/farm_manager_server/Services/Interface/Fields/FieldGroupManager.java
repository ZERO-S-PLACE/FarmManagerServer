package org.zeros.farm_manager_server.Services.Interface.Fields;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldGroupDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;

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
