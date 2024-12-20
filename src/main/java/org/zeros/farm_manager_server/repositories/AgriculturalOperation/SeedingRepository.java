package org.zeros.farm_manager_server.repositories.AgriculturalOperation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.Seeding;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeedingRepository extends JpaRepository<Seeding, UUID> {

    List<Seeding> findAllByFarmingMachine(FarmingMachine farmingMachine);
}
