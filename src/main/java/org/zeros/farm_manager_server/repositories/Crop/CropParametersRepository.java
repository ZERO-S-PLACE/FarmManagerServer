package org.zeros.farm_manager_server.repositories.Crop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.ResourceType;
import org.zeros.farm_manager_server.entities.Crops.CropParameters.CropParameters;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CropParametersRepository extends JpaRepository<CropParameters, UUID> {


    Page<CropParameters> findAllByCreatedByIn(Set<String> strings, Pageable pageable);

    Page<CropParameters> findAllByResourceTypeAndCreatedByIn(ResourceType resourceType, Set<String> strings, PageRequest name);

    Page<CropParameters> findAllByNameContainingAndCreatedByIn(String name, Set<String> strings, PageRequest name1);

    Optional<CropParameters> findAllByNameAndCreatedBy(@NonNull String name, @NonNull String createdBy);
}
