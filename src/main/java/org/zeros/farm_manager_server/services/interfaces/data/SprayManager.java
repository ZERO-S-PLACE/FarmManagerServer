package org.zeros.farm_manager_server.services.interfaces.data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.domain.dto.data.SprayDTO;
import org.zeros.farm_manager_server.domain.entities.data.Spray;
import org.zeros.farm_manager_server.domain.enums.SprayType;

import java.util.UUID;

public interface SprayManager {
    Page<SprayDTO> getAllSprays(int pageNumber);

    Page<SprayDTO> getDefaultSprays(int pageNumber);

    Page<SprayDTO> getUserSprays(int pageNumber);

    Page<SprayDTO> getSpraysByNameAs(@NotNull String name, int pageNumber);

    Page<SprayDTO> getSpraysByProducerAs(@NotNull String producer, int pageNumber);

    Page<SprayDTO> getSpraysBySprayType(@NotNull SprayType sprayType, int pageNumber);

    Page<SprayDTO> getSpraysByActiveSubstance(@NotNull String activeSubstance, int pageNumber);

    Page<SprayDTO> getSpraysCriteria(String name, String producer, SprayType sprayType, String activeSubstance, Integer pageNumber);

    SprayDTO getSprayById(@NotNull UUID uuid);

    SprayDTO addSpray(@NotNull SprayDTO sprayDTO);

    SprayDTO updateSpray(@NotNull SprayDTO sprayDTO);

    void deleteSpraySafe(@NotNull UUID sprayId);

    Spray getUndefinedSpray();

    Spray getSprayIfExists(UUID sprayId);
}
