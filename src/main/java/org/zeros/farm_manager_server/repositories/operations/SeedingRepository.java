package org.zeros.farm_manager_server.repositories.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.operations.Seeding;

import java.util.List;
import java.util.UUID;

public interface SeedingRepository extends JpaRepository<Seeding, UUID> {

    List<Seeding> findAllByFarmingMachine(FarmingMachine farmingMachine);

}
