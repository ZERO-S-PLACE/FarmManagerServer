package org.zeros.farm_manager_server.repositories.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.operations.Cultivation;

import java.util.List;
import java.util.UUID;

public interface CultivationRepository extends JpaRepository<Cultivation, UUID> {

    List<Cultivation> findAllByFarmingMachine(FarmingMachine farmingMachine);
}
