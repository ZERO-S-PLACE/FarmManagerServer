package org.zeros.farm_manager_server.DAO.Interface.Data;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Enum.SprayType;

import java.util.UUID;

public interface SprayManager {
    Page<Spray> getAllSprays(int pageNumber);

    Page<Spray> getDefaultSprays(int pageNumber);

    Page<Spray> getUserSprays(int pageNumber);

    Page<Spray> getSpraysByNameAs(String name, int pageNumber);

    Page<Spray> getSpraysByProducerAs(String producer, int pageNumber);

    Page<Spray> getSpraysBySprayType(SprayType sprayType, int pageNumber);

    Page<Spray> getSpraysByActiveSubstance(String activeSubstance, int pageNumber);

    Spray getSprayById(UUID uuid);

    Spray addSpray(Spray spray);

    Spray updateSpray(Spray spray);

    void deleteSpraySafe(Spray spray);

    Spray getUndefinedSpray();
}
