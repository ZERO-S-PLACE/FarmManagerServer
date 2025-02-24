package org.zeros.farm_manager_server.services.interfaces.crop;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.domain.dto.crop.CropSaleDTO;

import java.util.UUID;

public interface CropSaleManager {

    CropSaleDTO addCropSale(@NotNull UUID cropId, @NotNull CropSaleDTO cropSaleDTO);

    CropSaleDTO updateCropSale(@NotNull CropSaleDTO cropSaleDTO);

    void deleteCropSale(@NotNull UUID cropSaleId);

    CropSaleDTO getCropSaleById(@NotNull UUID id);
}
