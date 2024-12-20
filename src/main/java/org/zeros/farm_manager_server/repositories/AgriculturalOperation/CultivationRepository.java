package org.zeros.farm_manager_server.repositories.AgriculturalOperation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.Cultivation;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CultivationRepository extends JpaRepository<Cultivation, UUID> {
    @NonNull
    Optional<Cultivation> findById(@NotNull UUID Id);
    List<Cultivation> findAllByCrop(@NotNull Crop crop);


    List<Cultivation> findAllByFarmingMachine(FarmingMachine farmingMachine);
}
