package org.zeros.farm_manager_server.Services.Interface.Operations;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;

import java.util.UUID;

public interface AgriculturalOperationsManager {

    AgriculturalOperationDTO getOperationById(@NotNull UUID operationId, @NotNull OperationType operationType);

    AgriculturalOperationDTO planOperation(@NotNull UUID cropId, @NotNull AgriculturalOperationDTO agriculturalOperationDTO);

    AgriculturalOperationDTO addOperation(@NotNull UUID cropId, @NotNull AgriculturalOperationDTO agriculturalOperationDTO);

    void setPlannedOperationPerformed(@NotNull UUID operationId, @NotNull OperationType operationType);

    void updateOperationMachine(@NotNull UUID operationId, @NotNull OperationType operationType, @NotNull UUID machineId);

    void updateOperationParameters(@NotNull AgriculturalOperationDTO agriculturalOperationDTO);

    void deleteOperation(@NotNull UUID operationId, @NotNull OperationType operationType);

}
