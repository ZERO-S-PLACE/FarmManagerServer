package org.zeros.farm_manager_server.Services.Interface.Crop;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;

import java.util.UUID;

public interface CropSaleManager {

    CropSaleDTO  addCropSale(@NotNull UUID cropId, @NotNull CropSaleDTO cropSaleDTO);

    CropSaleDTO  updateCropSale(@NotNull CropSaleDTO cropSaleDTO);

    void deleteCropSale(@NotNull UUID cropSaleId);

    CropSaleDTO  getCropSaleById(@NotNull UUID id);
}
