package org.zeros.farm_manager_server.repositories.Data;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SpeciesRepository extends JpaRepository<Species, UUID> {

    Page<Species> findAllByNameContainsIgnoreCaseAndCreatedByIn(String name,Set<String> createdBy, Pageable pageable);
    Page<Species> findAllByFamilyContainsIgnoreCaseAndCreatedByIn(String name,Set<String> createdBy, Pageable pageable);
    Optional<Species> findByNameAndFamilyAndCreatedByIn(@NonNull String name, @NotNull String family,Set<String> createdBy);
    Optional<Species> getSpeciesByNameAndCreatedByIn(String name,Set<String> createdBy);
    Page<Species> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    Optional<Species> getSpeciesByName(String rapeSeed);
}

