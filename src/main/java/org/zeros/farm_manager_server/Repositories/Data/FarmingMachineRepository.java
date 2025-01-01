package org.zeros.farm_manager_server.Repositories.Data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FarmingMachineRepository extends JpaRepository<FarmingMachine, UUID> {


    Page<FarmingMachine> findAllByModelContainingIgnoreCaseAndCreatedByIn(String model, Set<String> createdBy, Pageable pageable);

    Page<FarmingMachine> findAllByProducerContainingIgnoreCaseAndCreatedByIn(String producer, Set<String> createdBy, Pageable pageable);

    Page<FarmingMachine> findAllByProducerContainingIgnoreCaseAndModelContainingIgnoreCaseAndCreatedByIn(String producer, String model, Set<String> createdBy, Pageable pageable);

    Optional<FarmingMachine> findByProducerAndModelAndCreatedByIn(String producer, String model, Set<String> createdBy);

    Page<FarmingMachine> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    Page<FarmingMachine> findAllBySupportedOperationTypesContainsAndCreatedByIn(OperationType operationType, Set<String> strings, PageRequest model);

    void deleteAllByCreatedBy(String username);
}
