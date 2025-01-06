package org.zeros.farm_manager_server.Services.Interface.Crop;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSummary.CropSummary;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSummary.ResourcesSummary;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;

import java.util.Map;
import java.util.UUID;

public interface CropDataReader {

    CropSummary getCropSummary(@NotNull UUID cropId);

    ResourcesSummary getCropResourcesSummary(@NotNull UUID cropId);

    ResourcesSummary getPlannedResourcesSummary(@NotNull UUID cropId);

    Map<ResourceType, CropParametersDTO> getMeanCropParameters(@NotNull UUID cropId);
}
