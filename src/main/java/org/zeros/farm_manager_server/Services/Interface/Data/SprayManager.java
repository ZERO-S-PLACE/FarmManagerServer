package org.zeros.farm_manager_server.Services.Interface.Data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.Data.SprayDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Spray;
import org.zeros.farm_manager_server.Domain.Enum.SprayType;

import java.util.UUID;

public interface SprayManager {
    Page<Spray> getAllSprays(int pageNumber);

    Page<Spray> getDefaultSprays(int pageNumber);

    Page<Spray> getUserSprays(int pageNumber);

    Page<Spray> getSpraysByNameAs(@NotNull String name, int pageNumber);

    Page<Spray> getSpraysByProducerAs(@NotNull String producer, int pageNumber);

    Page<Spray> getSpraysBySprayType(@NotNull SprayType sprayType, int pageNumber);

    Page<Spray> getSpraysByActiveSubstance(@NotNull String activeSubstance, int pageNumber);

    Page<Spray> getSpraysCriteria(String name, String producer, SprayType sprayType, String activeSubstance, Integer pageNumber);

    Spray getSprayById(@NotNull UUID uuid);

    Spray addSpray(@NotNull SprayDTO sprayDTO);

    Spray updateSpray(@NotNull SprayDTO sprayDTO);

    void deleteSpraySafe(@NotNull UUID sprayId);

    Spray getUndefinedSpray();

    Spray getSprayIfExists(UUID sprayId);
}
