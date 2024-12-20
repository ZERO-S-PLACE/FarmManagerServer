package org.zeros.farm_manager_server.repositories.AgriculturalOperation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.FertilizerApplication;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FertilizerApplicationRepository extends JpaRepository<FertilizerApplication, UUID> {


    List<FertilizerApplication> findAllByFertilizer(Fertilizer fertilizer);

    List<FertilizerApplication> findAllByFarmingMachine(FarmingMachine farmingMachine);
}
