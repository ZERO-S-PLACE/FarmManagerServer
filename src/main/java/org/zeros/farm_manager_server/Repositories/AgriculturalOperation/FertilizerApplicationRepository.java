package org.zeros.farm_manager_server.Repositories.AgriculturalOperation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Entities.Operations.FertilizerApplication;

import java.util.List;
import java.util.UUID;

public interface FertilizerApplicationRepository extends JpaRepository<FertilizerApplication, UUID> {


    List<FertilizerApplication> findAllByFertilizer(Fertilizer fertilizer);

    List<FertilizerApplication> findAllByFarmingMachine(FarmingMachine farmingMachine);
}
