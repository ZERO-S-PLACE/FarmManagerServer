package org.zeros.farm_manager_server.Services.Interface.Data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Range;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.SprayDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.SprayType;

import java.util.UUID;

public interface SprayManager {
    Page<Spray> getAllSprays(int pageNumber);

    Page<Spray> getDefaultSprays(int pageNumber);

    Page<Spray> getUserSprays(int pageNumber);

    Page<Spray> getSpraysByNameAs(String name, int pageNumber);

    Page<Spray> getSpraysByProducerAs(String producer, int pageNumber);

    Page<Spray> getSpraysBySprayType(SprayType sprayType, int pageNumber);

    Page<Spray> getSpraysByActiveSubstance(String activeSubstance, int pageNumber);

    Page<Spray> getSpraysCriteria(String name, String producer, SprayType sprayType, String activeSubstance, Integer pageNumber);

    Spray getSprayById(UUID uuid);

    Spray addSpray(SprayDTO sprayDTO);

    Spray updateSpray(SprayDTO sprayDTO);

    void deleteSpraySafe(UUID sprayId);

    Spray getUndefinedSpray();

}
