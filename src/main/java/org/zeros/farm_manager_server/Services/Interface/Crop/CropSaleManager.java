package org.zeros.farm_manager_server.Services.Interface.Crop;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;

import java.util.UUID;

public interface CropSaleManager {

    CropSale addCropSale(@NotNull UUID cropId, @NotNull CropSaleDTO cropSaleDTO);

    CropSale updateCropSale(@NotNull CropSaleDTO cropSaleDTO);

    void removeCropSale(@NotNull UUID cropSaleId);

    CropSale getCropSaleById(@NotNull UUID id);
}
