package org.zeros.farm_manager_server.Repositories.AgriculturalOperation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.Cultivation;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.Crop;

import java.util.List;
import java.util.UUID;

public interface CultivationRepository extends JpaRepository<Cultivation, UUID> {

    List<Cultivation> findAllByCrop(Crop crop);

    List<Cultivation> findAllByFarmingMachine(FarmingMachine farmingMachine);
}
