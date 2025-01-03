package org.zeros.farm_manager_server.Repositories.Data;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Subside;

import java.time.LocalDate;
import java.util.*;

public interface SubsideRepository extends JpaRepository<Subside, UUID> {

    Page<Subside> findAllByNameContainingIgnoreCaseAndCreatedByIn(String name, Set<String> createdBy, Pageable pageable);

    Page<Subside> findAllBySpeciesAllowedContainsAndCreatedByIn(Species species, Set<String> createdBy, Pageable pageable);

    Optional<Subside> getSubsideById(UUID id);


    Page<Subside> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    List<Subside> findAllBySpeciesAllowedContains(Species species);


    void deleteAllByCreatedBy(String username);

    Page<Subside> findByNameAndYearOfSubsideAndCreatedByIn(String name,  LocalDate yearOfSubside, Collection<String> createdBy, Pageable pageable);
}
