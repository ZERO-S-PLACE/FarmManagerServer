package org.zeros.farm_manager_server.repositories.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.io.Serializable;
import java.util.*;

public interface FertilizerRepository extends JpaRepository<Fertilizer, UUID> {


    Page<Fertilizer> findAllByIsNaturalFertilizerAndCreatedByIn(boolean b, Set<Serializable> createdBy,Pageable pageable);

    Optional<Fertilizer> findByNameAndProducerAndCreatedByIn(String name, String producer,Set<Serializable> createdBy);

    Page<Fertilizer> findAllByCreatedByIn(Set<Serializable> createdBy, Pageable pageable);

    Page<Fertilizer> findAllByNameContainingAndCreatedByIn(String name, Set<String> admin,  Pageable pageable);
}
