package org.zeros.farm_manager_server.Controllers.Crop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSummary.CropSummary;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSummary.ResourcesSummary;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropDataReader;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Crop Data Reader", description = "API for reading crop-related data and summaries")
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

    @Operation(
            summary = "Get crop summary",
            description = "Retrieves a summary of a specific crop, including key details and statistics",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Crop summary retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CropSummary.class))),
                    @ApiResponse(responseCode = "404", description = "Crop not found")
            }
    )
    @GetMapping(CROP_SUMMARY_PATH)
    public CropSummary getCropSummary(
            @Parameter(description = "UUID of the crop") @RequestParam UUID cropId) {

        return cropDataReader.getCropSummary(cropId);
    }

    @Operation(
            summary = "Get crop resource summary",
            description = "Retrieves the summary of resources used by a specific crop",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Crop resource summary retrieved successfully",
                            content = @Content(schema = @Schema(implementation = ResourcesSummary.class))),
                    @ApiResponse(responseCode = "404", description = "Crop not found")
            }
    )
    @GetMapping(CROP_RESOURCES_PATH)
    public ResourcesSummary getCropResourcesSummary(
            @Parameter(description = "UUID of the crop") @RequestParam UUID cropId) {

        return cropDataReader.getCropResourcesSummary(cropId);
    }

    @Operation(
            summary = "Get planned resources for crop",
            description = "Retrieves the planned resources for a specific crop",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Planned resources retrieved successfully",
                            content = @Content(schema = @Schema(implementation = ResourcesSummary.class))),
                    @ApiResponse(responseCode = "404", description = "Crop not found")
            }
    )
    @GetMapping(CROP_PLANNED_RESOURCES_PATH)
    public ResourcesSummary getPlannedResourcesSummary(
            @Parameter(description = "UUID of the crop") @RequestParam UUID cropId) {

        return cropDataReader.getPlannedResourcesSummary(cropId);
    }

    @Operation(
            summary = "Get mean crop parameters",
            description = "Retrieves the mean crop parameters grouped by resource type",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mean crop parameters retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CropParametersDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Crop not found")
            }
    )
    @GetMapping(CROP_MEAN_PARAMETERS)
    public Map<ResourceType, CropParametersDTO> getMeanCropParameters(
            @Parameter(description = "UUID of the crop") @RequestParam UUID cropId) {

        return cropDataReader.getMeanCropParameters(cropId);
    }
}
