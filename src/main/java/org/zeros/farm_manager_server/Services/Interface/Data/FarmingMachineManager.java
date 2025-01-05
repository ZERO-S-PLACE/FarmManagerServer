package org.zeros.farm_manager_server.Services.Interface.Data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.Enum.OperationType;

import java.util.UUID;

public interface FarmingMachineManager {
    Page<FarmingMachine> getAllFarmingMachines(int pageNumber);

    Page<FarmingMachine> getDefaultFarmingMachines(int pageNumber);

    Page<FarmingMachine> getUserFarmingMachines(int pageNumber);

    Page<FarmingMachine> getFarmingMachineByNameAs(@NotNull String name, int pageNumber);

    Page<FarmingMachine> getFarmingMachineByProducerAs(@NotNull String producer, int pageNumber);

    Page<FarmingMachine> getFarmingMachineByProducerAndNameAs(@NotNull String producer, @NotNull String model, int pageNumber);

    Page<FarmingMachine> getFarmingMachineBySupportedOperation(@NotNull OperationType operationType, int pageNumber);

    Page<FarmingMachine> getFarmingMachineCriteria(String model, String producer, OperationType operationType, int pageNumber);

    FarmingMachine getFarmingMachineById(@NotNull UUID id);

    FarmingMachine addFarmingMachine(@NotNull FarmingMachineDTO farmingMachineDTO);

    FarmingMachine updateFarmingMachine(@NotNull FarmingMachineDTO farmingMachineDTO);

    void deleteFarmingMachineSafe(@NotNull UUID farmingMachineId);

    FarmingMachine getUndefinedFarmingMachine();
}
