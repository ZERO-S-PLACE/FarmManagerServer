package org.zeros.farm_manager_server.Repositories.Data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.Data.Spray;
import org.zeros.farm_manager_server.Domain.Enum.SprayType;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SprayRepository extends JpaRepository<Spray, UUID> {


    Page<Spray> findAllByNameContainingIgnoreCaseAndCreatedByIn(String name, Set<String> createdBy, Pageable pageable);

    Page<Spray> findAllByProducerContainingIgnoreCaseAndCreatedByIn(String producer, Set<String> createdBy,Pageable pageable);

    Page<Spray> findAllBySprayTypeAndCreatedByIn(SprayType sprayType, Set<String> createdBy,Pageable pageable);

    Page<Spray> findAllByActiveSubstancesContainsAndCreatedByIn(String activeSubstance, Set<String> createdBy, Pageable pageable);

    Page<Spray> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    Optional<Spray> findByNameAndSprayTypeAndCreatedByIn(String name, SprayType sprayType, Set<String> createdBy);

    void deleteAllByCreatedBy(String username);
}
