package org.zeros.farm_manager_server.Services.Interface.Data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Fertilizer;

import java.util.UUID;

public interface FertilizerManager {

    Page<Fertilizer> getAllFertilizers(int pageNumber);

    Page<Fertilizer> getDefaultFertilizers(int pageNumber);

    Page<Fertilizer> getUserFertilizers(int pageNumber);

    Page<Fertilizer> getFertilizerByNameAs(@NotNull String name, int pageNumber);

    Page<Fertilizer> getNaturalFertilizers(int pageNumber);

    Page<Fertilizer> getSyntheticFertilizers(int pageNumber);

    Page<Fertilizer> getFertilizersCriteria(@NotNull String name, Boolean isNatural, int pageNumber);

    Fertilizer getFertilizerById(@NotNull UUID id);

    Fertilizer addFertilizer(@NotNull FertilizerDTO fertilizerDTO);

    Fertilizer updateFertilizer(@NotNull FertilizerDTO fertilizerDTO);

    Fertilizer getFertilizerIfExists(UUID fertilizerId);

    void deleteFertilizerSafe(@NotNull UUID fertilizerId);

    Fertilizer getUndefinedFertilizer();
}
