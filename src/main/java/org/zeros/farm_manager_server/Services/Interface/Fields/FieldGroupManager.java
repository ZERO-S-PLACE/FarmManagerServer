package org.zeros.farm_manager_server.Services.Interface.Fields;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldGroupDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;

import java.util.Set;
import java.util.UUID;

public interface FieldGroupManager {
    FieldGroup createEmptyFieldGroup(@NotNull String fieldGroupName, @NotNull String description);

    FieldGroup getFieldGroupByName(@NotNull String groupName);

    FieldGroup getFieldGroupById(@NotNull UUID id);

    FieldGroup updateFieldGroup(@NotNull FieldGroupDTO groupDTO);

    Set<FieldGroup> getAllFieldGroups();

    void deleteFieldGroupWithFields(@NotNull UUID groupId);

    void deleteFieldGroupWithoutFields(@NotNull UUID groupId);

    void moveFieldsToAnotherGroup(@NotNull Set<UUID> fieldsIds, @NotNull UUID newGroupId);
}
