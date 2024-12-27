package org.zeros.farm_manager_server.repositories.AgriculturalOperation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.Harvest;
import org.zeros.farm_manager_server.entities.Crop.CropParameters.CropParameters;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HarvestRepository extends JpaRepository<Harvest, UUID> {

    List<Harvest> findAllByFarmingMachine(FarmingMachine farmingMachine);

    Optional<Object> findByCropParameters(CropParameters cropParameters);
}
