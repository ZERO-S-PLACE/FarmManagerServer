package org.zeros.farm_manager_server.Repositories.Crop.CropParameters;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CropParametersRepository extends JpaRepository<CropParameters, UUID> {


    Page<CropParameters> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    Page<CropParameters> findAllByResourceTypeAndCreatedByIn(ResourceType resourceType, Set<String> createdBy, Pageable pageable);

    Page<CropParameters> findAllByNameContainingIgnoreCaseAndCreatedByIn(String name, Set<String> strings, Pageable pageable);

    Page<CropParameters> findAllByNameContainingIgnoreCaseAndResourceTypeAndCreatedByIn(String name, ResourceType resourceType, Set<String> strings, Pageable pageable);

    Page<CropParameters> findAllByNameAndCreatedByIn(String name, Set<String> createdBy, Pageable pageable);

    Optional<CropParameters> findAllByNameAndCreatedBy(String name, String createdBy);
}
