package org.zeros.farm_manager_server.services.interfaces.crop;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.domain.dto.crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.domain.entities.crop.crop_parameters.CropParameters;
import org.zeros.farm_manager_server.domain.enums.ResourceType;

import java.util.UUID;

public interface CropParametersManager {
    Page<CropParametersDTO> getParametersByNameAndResourceType(@NotNull String name, @NotNull ResourceType resourceType, int pageNumber);

    Page<CropParametersDTO> getCropParametersCriteria(@NotNull String name, @NotNull ResourceType resourceType, int pageNumber);

    CropParametersDTO getCropParametersById(@NotNull UUID id);

    CropParametersDTO addCropParameters(@NotNull CropParametersDTO cropParametersDTO);

    CropParametersDTO updateCropParameters(@NotNull CropParametersDTO cropParametersDTO);

    Page<CropParametersDTO> getParametersByName(@NotNull String name, int pageNumber);

    Page<CropParametersDTO> getAllCropParameters(int pageNumber);

    Page<CropParametersDTO> getParametersByResourceType(@NotNull ResourceType resourceType, int pageNumber);

    void deleteCropParametersSafe(@NotNull UUID cropParametersId);

    CropParameters getCropParametersIfExist(UUID cropParametersId);

    CropParameters getUndefinedCropParameters();
}
