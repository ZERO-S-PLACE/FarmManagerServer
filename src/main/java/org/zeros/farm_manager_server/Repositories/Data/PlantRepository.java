package org.zeros.farm_manager_server.Repositories.Data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.Data.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;

import java.util.Set;
import java.util.UUID;

public interface PlantRepository extends JpaRepository<Plant, UUID> {


    Page<Plant> findAllBySpeciesAndCreatedByIn(Species species, Set<String> createdBy, Pageable pageable);

    Page<Plant> findAllByVarietyContainingIgnoreCaseAndCreatedByIn(String variety, Set<String> createdBy, Pageable pageable);

    Page<Plant> findAllBySpeciesAndVarietyContainingIgnoreCaseAndCreatedByIn(Species species, String variety, Set<String> createdBy, Pageable pageable);

    Page<Plant> findAllBySpeciesAndVarietyAndCreatedByIn(Species species, String variety, Set<String> createdBy, Pageable pageable);

    Page<Plant> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    void deleteAllByCreatedBy(String username);
}
