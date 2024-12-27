package org.zeros.farm_manager_server.DAO.Interface;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.entities.Crop.CropParameters.CropParameters;

import java.util.UUID;

public interface CropParametersManager {
    CropParameters getCropParametersById(UUID id);

    CropParameters createCropParameters(CropParameters cropParameters);

    CropParameters updateCropParameters(CropParameters cropParameters);

    Page<CropParameters> getParametersByName(String name, int pageNumber);

    Page<CropParameters> getAllCropParameters(int pageNumber);

    Page<CropParameters> getParametersByResourceType(ResourceType resourceType, int pageNumber);

    void deleteCropParametersSafe(CropParameters cropParameters);

    CropParameters getUndefinedCropParameters();
}
