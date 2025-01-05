package org.zeros.farm_manager_server.Controllers.Crop;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.CropSummary.CropSummary;
import org.zeros.farm_manager_server.Domain.DTO.CropSummary.ResourcesSummary;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropDataReader;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CropDataReaderController {
    public static final String BASE_PATH = "/api/user/crop";
    public static final String CROP_SUMMARY_PATH = BASE_PATH + "/SUMMARY";
    public static final String CROP_RESOURCES_PATH = BASE_PATH + "/RESOURCES";
    public static final String CROP_PLANNED_RESOURCES_PATH = BASE_PATH + "/RESOURCES/PLANNED";
    public static final String CROP_MEAN_PARAMETERS = BASE_PATH + "/MEAN_PARAMETERS";
    private final CropDataReader cropDataReader;

    @GetMapping(CROP_SUMMARY_PATH)
    public CropSummary getCropSummary(@RequestParam UUID cropId) {
        return cropDataReader.getCropSummary(cropId);
    }

    @GetMapping(CROP_RESOURCES_PATH)
    public ResourcesSummary getCropResourcesSummary(@RequestParam UUID cropId) {
        return cropDataReader.getCropResourcesSummary(cropId);
    }

    @GetMapping(CROP_PLANNED_RESOURCES_PATH)
    ResourcesSummary getPlannedResourcesSummary(@RequestParam UUID cropId) {
        return cropDataReader.getPlannedResourcesSummary(cropId);
    }

    @GetMapping(CROP_MEAN_PARAMETERS)
    Map<ResourceType, CropParametersDTO> getMeanCropParameters(@RequestParam UUID cropId) {
        return cropDataReader.getMeanCropParameters(cropId);
    }

}
