package org.zeros.farm_manager_server.Services.Interface.Data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Fertilizer;

import java.util.UUID;

public interface FertilizerManager {

    Page<FertilizerDTO> getAllFertilizers(int pageNumber);

    Page<FertilizerDTO> getDefaultFertilizers(int pageNumber);

    Page<FertilizerDTO> getUserFertilizers(int pageNumber);

    Page<FertilizerDTO> getFertilizerByNameAs(@NotNull String name, int pageNumber);

    Page<FertilizerDTO> getNaturalFertilizers(int pageNumber);

    Page<FertilizerDTO> getSyntheticFertilizers(int pageNumber);

    Page<FertilizerDTO> getFertilizersCriteria(@NotNull String name, Boolean isNatural, int pageNumber);

    FertilizerDTO getFertilizerById(@NotNull UUID id);

    FertilizerDTO addFertilizer(@NotNull FertilizerDTO fertilizerDTO);

    FertilizerDTO updateFertilizer(@NotNull FertilizerDTO fertilizerDTO);

    void deleteFertilizerSafe(@NotNull UUID fertilizerId);

    Fertilizer getFertilizerIfExists(UUID fertilizerId);

    Fertilizer getUndefinedFertilizer();
}
