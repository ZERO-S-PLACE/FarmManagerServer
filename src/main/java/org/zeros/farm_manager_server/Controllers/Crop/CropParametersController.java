package org.zeros.farm_manager_server.Controllers.Crop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropParametersManager;

import java.util.UUID;

@Tag(name = "Crop Parameters", description = "API for managing crop parameter records")
@Slf4j
@RequiredArgsConstructor
@RestController
public class CropParametersController {
    public static final String BASE_PATH = "/api/user/crop/parameters";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";

    private final CropParametersManager cropParametersManager;

    @Operation(
            summary = "Get crop parameters by ID",
            description = "Retrieves details of a specific crop parameter set",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Crop parameters retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CropParametersDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Crop parameters not found")
            }
    )
    @GetMapping(ID_PATH)
    public CropParametersDTO getById(
            @Parameter(description = "UUID of the crop parameters") @PathVariable UUID id) {

        return cropParametersManager.getCropParametersById(id);
    }

    @Operation(
            summary = "List all crop parameters",
            description = "Retrieves a paginated list of all crop parameters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of crop parameters retrieved successfully")
            }
    )
    @GetMapping(LIST_ALL_PATH)
    public Page<CropParametersDTO> getAll(
            @Parameter(description = "Page number for pagination", example = "0")
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {

        return cropParametersManager.getAllCropParameters(pageNumber);
    }

    @Operation(
            summary = "Search crop parameters",
            description = "Retrieves crop parameters based on search criteria (name and resource type)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered crop parameters retrieved successfully")
            }
    )
    @GetMapping(LIST_PARAM_PATH)
    public Page<CropParametersDTO> getCriteria(
            @Parameter(description = "Page number for pagination", example = "0")
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Name of the crop parameter")
            @RequestParam(required = false) String name,
            @Parameter(description = "Resource type of the crop parameter")
            @RequestParam(required = false) ResourceType resourceType) {

        return cropParametersManager.getCropParametersCriteria(name, resourceType, pageNumber);
    }

    @Operation(
            summary = "Create new crop parameters",
            description = "Adds a new crop parameters entry to the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Crop parameters created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PostMapping(BASE_PATH)
    public ResponseEntity<String> addNew(
            @RequestBody CropParametersDTO cropParametersDTO) {

        CropParametersDTO saved = cropParametersManager.addCropParameters(cropParametersDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update crop parameters",
            description = "Updates an existing crop parameters entry",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Crop parameters updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Crop parameters not found")
            }
    )
    @PatchMapping(BASE_PATH)
    public <T extends CropParametersDTO> ResponseEntity<String> update(
            @RequestBody T cropParametersDTO) {

        cropParametersManager.updateCropParameters(cropParametersDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete crop parameters by ID",
            description = "Removes a specific crop parameters entry",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Crop parameters deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Crop parameters not found")
            }
    )
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteById(
            @Parameter(description = "UUID of the crop parameters to delete")
            @RequestParam UUID id) {

        cropParametersManager.deleteCropParametersSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
