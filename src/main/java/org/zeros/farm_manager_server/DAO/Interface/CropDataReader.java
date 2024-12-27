package org.zeros.farm_manager_server.DAO.Interface;

import org.zeros.farm_manager_server.entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.entities.DataTransfer.CropSummary;
import org.zeros.farm_manager_server.entities.DataTransfer.ResourcesSummary;

import java.util.Map;

public interface CropDataReader {

    CropSummary getCropSummary(Crop crop);

    ResourcesSummary getCropResourcesSummary(Crop crop);

    ResourcesSummary getPlannedResourcesSummary(Crop crop);

    Map<ResourceType, CropParameters> getMeanCropParameters(Crop crop);
}
