package org.zeros.farm_manager_server.Repositories.Data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SpeciesRepository extends JpaRepository<Species, UUID> {

    Page<Species> findAllByNameContainsIgnoreCaseAndCreatedByIn(String name, Set<String> createdBy, Pageable pageable);

    Page<Species> findAllByFamilyContainsIgnoreCaseAndCreatedByIn(String name, Set<String> createdBy, Pageable pageable);

    Page<Species> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    Optional<Species> findByNameAndFamilyAndCreatedByIn(String name, String family, Set<String> createdBy);

    Optional<Species> getSpeciesByName(String rapeSeed);

    void deleteAllByCreatedBy(String username);

}

