package org.zeros.farm_manager_server.Repositories.Data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Entities.Crop.Subside;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SubsideRepository extends JpaRepository<Subside, UUID> {

    Page<Subside> findAllByNameContainingIgnoreCaseAndCreatedByIn(String name, Set<String> createdBy, Pageable pageable);

    Page<Subside> findAllBySpeciesAllowedContainsAndCreatedByIn(Species species, Set<String> createdBy, Pageable pageable);

    Optional<Subside> getSubsideById(UUID id);


    Page<Subside> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    List<Object> findAllBySpeciesAllowedContains(Species species);

    Optional<Subside> findByNameAndCreatedByIn(String name, Set<String> createdBy);

    void deleteAllByCreatedBy(String username);
}
