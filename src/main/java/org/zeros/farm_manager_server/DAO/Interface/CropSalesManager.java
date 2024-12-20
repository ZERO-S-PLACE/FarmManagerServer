package org.zeros.farm_manager_server.DAO.Interface;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crops.CropSale;

import java.util.UUID;

public interface CropSalesManager {
    CropSale getCropSaleById(UUID id);
    CropSale addCropSale(CropSale cropSale, Crop crop);
    CropSale updateCropSale(CropSale cropSale);
    void deleteCropSale(CropSale cropSale);
}
