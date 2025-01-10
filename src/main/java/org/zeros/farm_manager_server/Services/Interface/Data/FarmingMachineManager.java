package org.zeros.farm_manager_server.Services.Interface.Data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;

import java.util.UUID;

public interface FarmingMachineManager {
    Page<FarmingMachineDTO > getAllFarmingMachines(int pageNumber);

    Page<FarmingMachineDTO > getDefaultFarmingMachines(int pageNumber);

    Page<FarmingMachineDTO > getUserFarmingMachines(int pageNumber);

    Page<FarmingMachineDTO > getFarmingMachineByNameAs(@NotNull String name, int pageNumber);

    Page<FarmingMachineDTO > getFarmingMachineByProducerAs(@NotNull String producer, int pageNumber);

    Page<FarmingMachineDTO > getFarmingMachineByProducerAndNameAs(@NotNull String producer, @NotNull String model, int pageNumber);

    Page<FarmingMachineDTO > getFarmingMachineBySupportedOperation(@NotNull OperationType operationType, int pageNumber);

    Page<FarmingMachineDTO > getFarmingMachineCriteria(String model, String producer, OperationType operationType, int pageNumber);

    FarmingMachineDTO  getFarmingMachineById(@NotNull UUID id);

    FarmingMachineDTO  addFarmingMachine(@NotNull FarmingMachineDTO farmingMachineDTO);

    FarmingMachineDTO  updateFarmingMachine(@NotNull FarmingMachineDTO farmingMachineDTO);

    void deleteFarmingMachineSafe(@NotNull UUID farmingMachineId);

    FarmingMachine getUndefinedFarmingMachine();

    FarmingMachine getFarmingMachineIfCompatible(UUID farmingMachineId,@NotNull OperationType operationType);
}
