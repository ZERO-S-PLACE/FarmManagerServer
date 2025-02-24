package org.zeros.farm_manager_server.services.interfaces.crop;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.domain.dto.crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.domain.dto.crop.CropSummary.CropSummary;
import org.zeros.farm_manager_server.domain.dto.crop.CropSummary.ResourcesSummary;
import org.zeros.farm_manager_server.domain.enums.ResourceType;

import java.util.Map;
import java.util.UUID;

public interface CropDataReader {

    CropSummary getCropSummary(@NotNull UUID cropId);

    ResourcesSummary getCropResourcesSummary(@NotNull UUID cropId);

    ResourcesSummary getPlannedResourcesSummary(@NotNull UUID cropId);

    Map<ResourceType, CropParametersDTO> getMeanCropParameters(@NotNull UUID cropId);
}
