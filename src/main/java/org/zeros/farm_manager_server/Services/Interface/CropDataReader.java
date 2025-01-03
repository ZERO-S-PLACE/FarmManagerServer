package org.zeros.farm_manager_server.Services.Interface;

import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.DTO.DataTransfer.CropSummary;
import org.zeros.farm_manager_server.Domain.DTO.DataTransfer.ResourcesSummary;

import java.util.Map;
import java.util.UUID;

public interface CropDataReader {

    CropSummary getCropSummary(UUID cropId);

    ResourcesSummary getCropResourcesSummary(UUID cropId);

    ResourcesSummary getPlannedResourcesSummary(UUID cropId);

    Map<ResourceType, CropParameters> getMeanCropParameters(UUID cropId);
}
