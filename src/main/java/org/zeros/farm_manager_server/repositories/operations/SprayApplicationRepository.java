package org.zeros.farm_manager_server.repositories.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.data.Fertilizer;
import org.zeros.farm_manager_server.domain.entities.data.Spray;
import org.zeros.farm_manager_server.domain.entities.operations.SprayApplication;

import java.util.List;
import java.util.UUID;

public interface SprayApplicationRepository extends JpaRepository<SprayApplication, UUID> {

    List<SprayApplication> findAllBySpray(Spray spray);

    List<SprayApplication> findAllByFertilizer(Fertilizer fertilizer);

    List<SprayApplication> findAllByFarmingMachine(FarmingMachine farmingMachine);
}
