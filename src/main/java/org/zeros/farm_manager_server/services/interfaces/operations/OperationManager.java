package org.zeros.farm_manager_server.services.interfaces.operations;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.domain.dto.operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.operations.AgriculturalOperation;
import org.zeros.farm_manager_server.domain.entities.operations.Seeding;
import org.zeros.farm_manager_server.exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;

import java.util.UUID;

public interface OperationManager<E extends AgriculturalOperation, D extends AgriculturalOperationDTO> {

    E getOperationById(@NotNull UUID id);

    E planOperation(@NotNull UUID cropId, @NotNull D operationDTO);

    E addOperation(@NotNull UUID cropId, @NotNull D operationDTO);

    E updateOperation(@NotNull D operationDTO);

    void deleteOperation(@NotNull UUID operationId);


    default AgriculturalOperation rewriteNotModifiedParameters(AgriculturalOperation entity, AgriculturalOperation entityParsed) {
        entityParsed.setCrop(entity.getCrop());
        entityParsed.setIsPlannedOperation(entity.getIsPlannedOperation());
        entityParsed.setFarmingMachine(entity.getFarmingMachine());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        return entityParsed;
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
