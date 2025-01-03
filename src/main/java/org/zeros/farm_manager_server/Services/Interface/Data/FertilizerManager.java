package org.zeros.farm_manager_server.Services.Interface.Data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Range;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;

import java.util.UUID;

public interface FertilizerManager {

    Page<Fertilizer> getAllFertilizers(int pageNumber);

    Page<Fertilizer> getDefaultFertilizers(int pageNumber);

    Page<Fertilizer> getUserFertilizers(int pageNumber);

    Page<Fertilizer> getFertilizerByNameAs(String name, int pageNumber);

    Page<Fertilizer> getNaturalFertilizers(int pageNumber);

    Page<Fertilizer> getSyntheticFertilizers(int pageNumber);

    Page<Fertilizer> getFertilizersCriteria(String name, Boolean isNatural, int pageNumber);

    Fertilizer getFertilizerById(UUID id);

    Fertilizer addFertilizer(FertilizerDTO fertilizerDTO);

    Fertilizer updateFertilizer(FertilizerDTO fertilizerDTO);

    void deleteFertilizerSafe(UUID fertilizerId);

    Fertilizer getUndefinedFertilizer();
}
