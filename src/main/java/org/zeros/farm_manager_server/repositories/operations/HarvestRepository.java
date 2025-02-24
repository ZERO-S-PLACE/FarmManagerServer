package org.zeros.farm_manager_server.repositories.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.domain.entities.crop.crop_parameters.CropParameters;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.operations.Harvest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HarvestRepository extends JpaRepository<Harvest, UUID> {

    List<Harvest> findAllByFarmingMachine(FarmingMachine farmingMachine);

    Optional<Harvest> findByCropParameters(CropParameters cropParameters);
}
