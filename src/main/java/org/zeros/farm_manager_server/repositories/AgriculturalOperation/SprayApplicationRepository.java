package org.zeros.farm_manager_server.repositories.AgriculturalOperation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.SprayApplication;

import java.util.List;
import java.util.UUID;

public interface SprayApplicationRepository extends JpaRepository<SprayApplication, UUID> {

    List<SprayApplication> findAllBySpray(Spray spray);

    List<SprayApplication> findAllByFertilizer(Fertilizer fertilizer);

    List<SprayApplication> findAllByFarmingMachine(FarmingMachine farmingMachine);
}
