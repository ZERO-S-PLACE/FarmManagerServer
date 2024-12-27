package org.zeros.farm_manager_server.Services.Interface.Data;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data.Fertilizer;

import java.util.UUID;

public interface FertilizerManager {

    Page<Fertilizer> getAllFertilizers(int pageNumber);

    Page<Fertilizer> getDefaultFertilizers(int pageNumber);

    Page<Fertilizer> getUserFertilizers(int pageNumber);

    Page<Fertilizer> getFertilizerByNameAs(String name, int pageNumber);

    Page<Fertilizer> getNaturalFertilizers(int pageNumber);

    Page<Fertilizer> getSyntheticFertilizers(int pageNumber);

    Fertilizer getFertilizerById(UUID id);

    Fertilizer addFertilizer(Fertilizer fertilizer);

    Fertilizer updateFertilizer(Fertilizer fertilizer);

    void deleteFertilizerSafe(Fertilizer fertilizer);

    Fertilizer getUndefinedFertilizer();
}
