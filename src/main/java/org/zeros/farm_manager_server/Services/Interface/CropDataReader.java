package org.zeros.farm_manager_server.Services.Interface;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.DTO.DataTransfer.CropSummary;
import org.zeros.farm_manager_server.Domain.DTO.DataTransfer.ResourcesSummary;

import java.util.Map;
import java.util.UUID;

public interface CropDataReader {

    CropSummary getCropSummary(@NotNull UUID cropId);

    ResourcesSummary getCropResourcesSummary(@NotNull UUID cropId);

    ResourcesSummary getPlannedResourcesSummary(@NotNull UUID cropId);

    Map<ResourceType, CropParameters> getMeanCropParameters(@NotNull UUID cropId);
}
