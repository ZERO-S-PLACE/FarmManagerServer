package org.zeros.farm_manager_server.Services.Interface.CropParameters;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.CropParameters;

import java.util.UUID;

public interface CropParametersManager {
    Page<CropParameters> getParametersByNameAndResourceType(@NotNull String name, @NotNull ResourceType resourceType, int pageNumber);

    Page<CropParameters> getCropParametersCriteria(@NotNull String name, @NotNull ResourceType resourceType, int pageNumber);

    CropParameters getCropParametersById(@NotNull UUID id);

    CropParameters addCropParameters(@NotNull CropParametersDTO cropParametersDTO);

    CropParameters updateCropParameters(@NotNull CropParametersDTO cropParametersDTO);

    Page<CropParameters> getParametersByName(@NotNull String name, int pageNumber);

    Page<CropParameters> getAllCropParameters(int pageNumber);

    Page<CropParameters> getParametersByResourceType(@NotNull ResourceType resourceType, int pageNumber);

    void deleteCropParametersSafe(@NotNull UUID cropParametersId);

    CropParameters getUndefinedCropParameters();
}
