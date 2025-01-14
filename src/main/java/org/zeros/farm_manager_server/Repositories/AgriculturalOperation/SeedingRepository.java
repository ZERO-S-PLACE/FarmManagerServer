package org.zeros.farm_manager_server.Repositories.AgriculturalOperation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.Operations.Seeding;

import java.util.List;
import java.util.UUID;

public interface SeedingRepository extends JpaRepository<Seeding, UUID> {

    List<Seeding> findAllByFarmingMachine(FarmingMachine farmingMachine);

}
