package org.zeros.farm_manager_server.repositories.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PlantRepository extends JpaRepository<Plant, UUID> {


    Page<Plant> findAllBySpeciesAndCreatedByIn(Species species,Set<String> createdBy, Pageable pageable);
    Page<Plant> findAllByVarietyContainingIgnoreCaseAndCreatedByIn(@NonNull String variety,Set<String> createdBy, Pageable pageable);
    Page<Plant> findAllBySpeciesAndVarietyContainingIgnoreCaseAndCreatedByIn(@NonNull Species species, @NonNull String variety,Set<String> createdBy, Pageable pageable);
    Page<Plant> findAllBySpeciesAndVarietyAndCreatedByIn(@NonNull Species species, @NonNull String variety, Set<String> createdBy,Pageable pageable);

    Page<Plant> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);
}
