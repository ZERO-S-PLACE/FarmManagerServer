package org.zeros.farm_manager_server.Services.Interface;

import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Entities.DataTransfer.CropSummary;
import org.zeros.farm_manager_server.Entities.DataTransfer.ResourcesSummary;

import java.util.Map;

public interface CropDataReader {

    CropSummary getCropSummary(Crop crop);

    ResourcesSummary getCropResourcesSummary(Crop crop);

    ResourcesSummary getPlannedResourcesSummary(Crop crop);

    Map<ResourceType, CropParameters> getMeanCropParameters(Crop crop);
}
