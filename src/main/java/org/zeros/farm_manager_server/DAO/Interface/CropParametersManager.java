package org.zeros.farm_manager_server.DAO.Interface;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.Harvest;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crops.CropParameters.CropParameters;
import org.zeros.farm_manager_server.entities.Crops.CropSale;

import java.util.UUID;

public interface CropParametersManager {
    CropSale getCropParametersById(UUID id);
    CropParameters addCropParametersToHarvest(CropParameters cropParameters, Harvest harvest);
    CropParameters addCropParametersToCropSale(CropParameters cropParameters, CropSale cropSale);
    CropParameters updateCropParameters(CropParameters cropParameters);
    CropParameters deleteCropParametersSafe(CropParameters cropParameters);


}
