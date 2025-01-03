package org.zeros.farm_manager_server.Repositories.Crop;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CropParametersRepository extends JpaRepository<CropParameters, UUID> {


    Page<CropParameters> findAllByCreatedByIn(Set<String> createdBy, Pageable pageable);

    Page<CropParameters> findAllByResourceTypeAndCreatedByIn(ResourceType resourceType, Set<String> createdBy, Pageable pageable);

    Page<CropParameters> findAllByNameContainingAndCreatedByIn(String name, Set<String> strings,Pageable pageable);

    Page<CropParameters> findAllByNameAndCreatedByIn( String name,  Set<String> createdBy,Pageable pageable);

    Optional<CropParameters> findAllByNameAndCreatedBy(String name, String createdBy);
}
