package org.zeros.farm_manager_server.Repositories.AgriculturalOperation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.Harvest;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HarvestRepository extends JpaRepository<Harvest, UUID> {

    List<Harvest> findAllByFarmingMachine(FarmingMachine farmingMachine);

    Optional<Object> findByCropParameters(CropParameters cropParameters);
}
