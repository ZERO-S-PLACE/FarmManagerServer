package org.zeros.farm_manager_server.repositories.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.SprayType;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SprayRepository extends JpaRepository<Spray, UUID> {


    Page<Spray> findAllByNameContainingIgnoreCaseAndCreatedByIn(String name,Set<String> createdBy, PageRequest name1);

    Page<Spray> findAllByProducerContainingIgnoreCaseAndCreatedByIn(String producer,Set<String> createdBy, PageRequest name);

    Page<Spray> findAllBySprayTypeAndCreatedByIn(SprayType sprayType,Set<String> createdBy, PageRequest name);

    Page<Spray> findAllByActiveSubstancesContainsAndCreatedByIn(String activeSubstance,Set<String> createdBy, PageRequest name);

    Optional<Object> findByNameAndProducerAndCreatedByIn(String name, String producer,Set<String> createdBy);

    Page<Spray> findAllByCreatedByIn(Set<String> createdBy, PageRequest name);
}
