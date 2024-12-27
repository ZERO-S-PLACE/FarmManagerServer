package org.zeros.farm_manager_server.DAO.Interface.Data;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Enum.OperationType;

import java.util.UUID;

public interface FarmingMachineManager {
    Page<FarmingMachine> getAllFarmingMachines(int pageNumber);

    Page<FarmingMachine> getDefaultFarmingMachines(int pageNumber);

    Page<FarmingMachine> getUserFarmingMachines(int pageNumber);

    Page<FarmingMachine> getFarmingMachineByNameAs(String name, int pageNumber);

    Page<FarmingMachine> getFarmingMachineByProducerAs(String producer, int pageNumber);

    Page<FarmingMachine> getFarmingMachineByProducerAndNameAs(String producer, String model, int pageNumber);

    Page<FarmingMachine> getFarmingMachineBySupportedOperation(OperationType operationType, int pageNumber);

    FarmingMachine getFarmingMachineById(UUID id);

    FarmingMachine addFarmingMachine(FarmingMachine farmingMachine);

    FarmingMachine updateFarmingMachine(FarmingMachine farmingMachine);

    void deleteFarmingMachineSafe(FarmingMachine farmingMachine);

    FarmingMachine getUndefinedFarmingMachine();
}
