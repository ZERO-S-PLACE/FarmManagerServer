package org.zeros.farm_manager_server.Repositories.Data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Entities.Data.Subside;

import java.time.LocalDate;
import java.util.*;

public interface SubsideRepository extends JpaRepository<Subside, UUID> {

    Page<Subside> findAllByNameContainingIgnoreCaseAndCreatedByIn(String name, Set<String> createdBy, Pageable pageable);

    Page<Subside> findAllBySpeciesAllowedContainsAndCreatedByIn(Species species, Set<String> createdBy, Pageable pageable);

    Page<Subside> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    Page<Subside> findByNameAndYearOfSubsideAndCreatedByIn(String name, LocalDate yearOfSubside, Collection<String> createdBy, Pageable pageable);

    Page<Subside> findAllByNameContainingIgnoreCaseAndSpeciesAllowedContainsAndCreatedByIn(String name, Species species, Set<String> strings, Pageable pageable);

    List<Subside> findAllBySpeciesAllowedContains(Species species);

    void deleteAllByCreatedBy(String username);


}
