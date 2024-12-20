package org.zeros.farm_manager_server.DAO.Interface;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.Crops.Subside;

import java.util.UUID;

public interface FarmingMachineManager {
    Page<FarmingMachine> getAllFarmingMachines(int pageNumber);
    Page<FarmingMachine> getDefaultFarmingMachines(int pageNumber);
    Page<FarmingMachine> getUserFarmingMachines(int pageNumber);
    Page<FarmingMachine> getFarmingMachineByNameAs(String name,int pageNumber);
    Page<FarmingMachine> getFarmingMachineByProducerAs(String producer,int pageNumber);
    Page<FarmingMachine> getFarmingMachineByProducerAndNameAs(String producer,String model, int pageNumber);
    FarmingMachine getFarmingMachineById(UUID id);
    FarmingMachine addFarmingMachine(FarmingMachine farmingMachine);
    FarmingMachine updateFarmingMachine(FarmingMachine farmingMachine);
    void deleteFarmingMachineSafe(FarmingMachine farmingMachine);
}
