package org.zeros.farm_manager_server.Repositories.Data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FertilizerRepository extends JpaRepository<Fertilizer, UUID> {


    Page<Fertilizer> findAllByIsNaturalFertilizerAndCreatedByIn(boolean b, Set<String> createdBy, Pageable pageable);

    Optional<Fertilizer> findByNameAndProducerAndCreatedByIn(String name, String producer, Set<String> createdBy);

    Page<Fertilizer> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    Page<Fertilizer> findAllByNameContainingAndCreatedByIn(String name, Set<String> admin, Pageable pageable);

    void deleteAllByCreatedBy(String username);
}
