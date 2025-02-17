package org.zeros.farm_manager_server.Controllers.Crop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Tag(name = "6.Crop Management", description = "API for managing crop records")
@Slf4j
@RequiredArgsConstructor
@RestController
public class CropController {
    public static final String BASE_PATH = "/api/user/crop";
    public static final String ID_PATH = BASE_PATH + "/{cropId}";
    public static final String MAIN_CROP_PATH = BASE_PATH + "/main";
    public static final String INTER_CROP_PATH = BASE_PATH + "/inter";
    public static final String WORK_FINISHED_PATH = BASE_PATH + "/SET_FINISHED";
    public static final String ADD_SUBSIDE_PATH = BASE_PATH + "/subside/ADD";
    public static final String REMOVE_SUBSIDE_PATH = BASE_PATH + "/subside/REMOVE";
    public static final String CROP_PLANTS_PATH = BASE_PATH + "/plants";

    private final CropManager cropManager;

    @Operation(
            summary = "Get crop by ID",
            description = "Retrieve details of a specific crop",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Crop details retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CropDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Crop not found")
            }
    )
    @GetMapping(ID_PATH)
    public CropDTO getById(@Parameter(description = "UUID of the crop to retrieve") @PathVariable("cropId") UUID cropId) {
        return cropManager.getCropById(cropId);
    }

    @Operation(summary = "Create a new main crop", description = "Registers a new main crop for a field part")
    @PostMapping(MAIN_CROP_PATH)
    public ResponseEntity<String> createNewMainCrop(
            @Parameter(description = "UUID of the field part where the crop is grown") @RequestParam UUID fieldPartId,
            @Parameter(description = "Set of cultivated plant UUIDs") @RequestParam Set<UUID> cultivatedPlantsIds) {

        CropDTO saved = cropManager.createNewMainCrop(fieldPartId, cultivatedPlantsIds);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(summary = "Create a new inter crop", description = "Registers a new inter crop")
    @PostMapping(INTER_CROP_PATH)
    public ResponseEntity<String> createNewInterCrop(
            @Parameter(description = "UUID of the field part where the inter crop is grown") @RequestParam UUID fieldPartId,
            @Parameter(description = "Set of cultivated plant UUIDs") @RequestParam Set<UUID> cultivatedPlantsIds) {

        CropDTO saved = cropManager.createNewInterCrop(fieldPartId, cultivatedPlantsIds);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(summary = "Update cultivated plants", description = "Updates the list of cultivated plants for a crop")
    @PatchMapping(CROP_PLANTS_PATH)
    public ResponseEntity<String> updateCultivatedPlants(
            @Parameter(description = "UUID of the crop to update") @RequestParam UUID cropId,
            @Parameter(description = "Set of new cultivated plant UUIDs") @RequestParam Set<UUID> cultivatedPlantsIds) {

        cropManager.updateCultivatedPlants(cropId, cultivatedPlantsIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Add a subside to a crop", description = "Links a subside to a specific crop")
    @PatchMapping(ADD_SUBSIDE_PATH)
    public ResponseEntity<String> addSubside(
            @Parameter(description = "UUID of the crop") @RequestParam UUID cropId,
            @Parameter(description = "UUID of the subside") @RequestParam UUID subsideId) {

        cropManager.addSubside(cropId, subsideId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Remove a subside from a crop", description = "Detaches a subside from a specific crop")
    @PatchMapping(REMOVE_SUBSIDE_PATH)
    public ResponseEntity<String> removeSubside(
            @Parameter(description = "UUID of the crop") @RequestParam UUID cropId,
            @Parameter(description = "UUID of the subside") @RequestParam UUID subsideId) {

        cropManager.removeSubside(cropId, subsideId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Set inter crop destruction date", description = "Registers the date an inter crop was destroyed")
    @PatchMapping(INTER_CROP_PATH)
    public ResponseEntity<String> setDateDestroyed(
            @Parameter(description = "UUID of the inter crop") @RequestParam UUID interCropId,
            @Parameter(description = "Date when the inter crop was destroyed") @RequestParam LocalDate dateDestroyed) {

        cropManager.setDateDestroyed(interCropId, dateDestroyed);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Mark crop work as finished", description = "Marks a crop's work as completed")
    @PatchMapping(WORK_FINISHED_PATH)
    public ResponseEntity<String> setWorkFinished(
            @Parameter(description = "UUID of the finished crop") @RequestParam UUID finishedCropId) {

        cropManager.setWorkFinished(finishedCropId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Mark main crop as fully sold", description = "Indicates that a main crop has been completely sold")
    @PatchMapping(MAIN_CROP_PATH)
    public ResponseEntity<String> setFullySold(
            @Parameter(description = "UUID of the fully sold crop") @RequestParam UUID fullySoldCropId) {

        cropManager.setFullySold(fullySoldCropId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete a crop", description = "Removes a crop and its associated data from the system")
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteCrop(
            @Parameter(description = "UUID of the crop to delete") @RequestParam UUID cropId) {

        cropManager.deleteCropAndItsData(cropId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
