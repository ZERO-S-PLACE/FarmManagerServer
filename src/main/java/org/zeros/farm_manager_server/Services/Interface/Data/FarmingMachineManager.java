package org.zeros.farm_manager_server.Services.Interface.Data;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;

import java.util.UUID;

public interface FarmingMachineManager {
    Page<FarmingMachine> getAllFarmingMachines(int pageNumber);

    Page<FarmingMachine> getDefaultFarmingMachines(int pageNumber);

    Page<FarmingMachine> getUserFarmingMachines(int pageNumber);

    Page<FarmingMachine> getFarmingMachineByNameAs(String name, int pageNumber);

    Page<FarmingMachine> getFarmingMachineByProducerAs(String producer, int pageNumber);

    Page<FarmingMachine> getFarmingMachineByProducerAndNameAs(String producer, String model, int pageNumber);

    Page<FarmingMachine> getFarmingMachineBySupportedOperation(OperationType operationType, int pageNumber);

    Page<FarmingMachine> getFarmingMachineCriteria( String model,String producer,OperationType operationType, int pageNumber);

    FarmingMachine getFarmingMachineById(UUID id);

    FarmingMachine addFarmingMachine(FarmingMachineDTO farmingMachineDTO);

    FarmingMachine updateFarmingMachine(FarmingMachineDTO farmingMachineDTO);

    void deleteFarmingMachineSafe(UUID farmingMachineId);

    FarmingMachine getUndefinedFarmingMachine();
}
