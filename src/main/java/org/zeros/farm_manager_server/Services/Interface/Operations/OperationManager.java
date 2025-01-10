package org.zeros.farm_manager_server.Services.Interface.Operations;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Entities.Operations.Seeding;
import org.zeros.farm_manager_server.Exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.Exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;

import java.util.UUID;

public interface OperationManager<D extends AgriculturalOperationDTO> {

    D getOperationById(@NotNull UUID id);

    D planOperation(@NotNull UUID cropId, @NotNull D operationDTO);

    D addOperation(@NotNull UUID cropId, @NotNull D operationDTO);

    D updateOperation(@NotNull D operationDTO);

    void deleteOperation(@NotNull UUID operationId);


    default void rewriteNotModifiedParameters(AgriculturalOperation entity, AgriculturalOperation entityParsed) {
        entityParsed.setCrop(entity.getCrop());
        entityParsed.setIsPlannedOperation(entity.getIsPlannedOperation());
        entityParsed.setFarmingMachine(entity.getFarmingMachine());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        entityParsed.setCreatedDate(entity.getCreatedDate());
    }

    default void checkOperationModificationAccess(Crop crop) {
        if (crop.getWorkFinished()) {
            throw new IllegalAccessErrorCustom(Crop.class, IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
        }
    }
    default void checkIfUUIDPresent(AgriculturalOperationDTO operationDTO) {
        if (operationDTO.getId() != null) {
            throw new IllegalArgumentExceptionCustom(Seeding.class, IllegalArgumentExceptionCause.OBJECT_EXISTS);
        }
    }




}
