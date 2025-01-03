package org.zeros.farm_manager_server.Services.Interface;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;

import java.util.UUID;

public interface CropParametersManager {
    CropParameters getCropParametersById(UUID id);

    CropParameters createCropParameters(CropParametersDTO cropParametersDTO);

    CropParameters updateCropParameters(CropParametersDTO cropParametersDTO);

    Page<CropParameters> getParametersByName(String name, int pageNumber);

    Page<CropParameters> getAllCropParameters(int pageNumber);

    Page<CropParameters> getParametersByResourceType(ResourceType resourceType, int pageNumber);

    void deleteCropParametersSafe(UUID cropParametersId);

    CropParameters getUndefinedCropParameters();
}
