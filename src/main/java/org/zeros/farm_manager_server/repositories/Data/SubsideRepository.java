package org.zeros.farm_manager_server.repositories.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.Crops.Subside;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.util.*;

public interface SubsideRepository extends JpaRepository<Subside, UUID> {

   @NonNull
   Page<Subside> findAll(@NonNull Pageable pageable);
   Page<Subside> findAllByNameContainingIgnoreCaseAndCreatedByIn(String name,Set<String> createdBy, @NonNull Pageable pageable);

   Page<Subside> findAllBySpeciesAllowedContainsAndCreatedByIn(Species species,Set<String> createdBy,Pageable pageable);

   Optional<Subside> getSubsideById(UUID id);


    Page<Subside> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

   List<Object> findAllBySpeciesAllowedContains(Species species);

   Optional<Subside> findByNameAndCreatedByIn(String name,Set<String> createdBy);
}
