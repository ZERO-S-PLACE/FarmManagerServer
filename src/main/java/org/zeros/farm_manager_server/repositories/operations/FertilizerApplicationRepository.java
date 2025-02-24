package org.zeros.farm_manager_server.repositories.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.data.Fertilizer;
import org.zeros.farm_manager_server.domain.entities.operations.FertilizerApplication;

import java.util.List;
import java.util.UUID;

public interface FertilizerApplicationRepository extends JpaRepository<FertilizerApplication, UUID> {


    List<FertilizerApplication> findAllByFertilizer(Fertilizer fertilizer);

    List<FertilizerApplication> findAllByFarmingMachine(FarmingMachine farmingMachine);
}
