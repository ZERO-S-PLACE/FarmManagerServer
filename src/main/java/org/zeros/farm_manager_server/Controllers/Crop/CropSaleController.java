package org.zeros.farm_manager_server.Controllers.Crop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropSaleManager;

import java.util.UUID;

@Tag(name = "Crop Sale Management", description = "API for managing crop sale records")
@Slf4j
@RequiredArgsConstructor
@RestController
public class CropSaleController {
    public static final String BASE_PATH = "/api/user/crop/sale";
    public static final String ID_PATH = BASE_PATH + "/{id}";

    private final CropSaleManager cropSaleManager;

    @Operation(
            summary = "Get crop sale by ID",
            description = "Retrieve details of a specific crop sale by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved crop sale details",
                            content = @Content(schema = @Schema(implementation = CropSaleDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Crop sale not found")
            }
    )
    @GetMapping(ID_PATH)
    public CropSaleDTO getById(@Parameter(description = "UUID of the crop sale to retrieve") @PathVariable("id") UUID id) {
        return cropSaleManager.getCropSaleById(id);
    }

    @Operation(
            summary = "Add a new crop sale",
            description = "Create a new crop sale record by providing crop ID and sale details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created a new crop sale"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PostMapping(BASE_PATH)
    public ResponseEntity<String> addNewCropSale(
            @Parameter(description = "UUID of the crop being sold") @RequestParam UUID cropId,
            @Parameter(description = "Details of the crop sale") @RequestBody CropSaleDTO cropSaleDTO) {

        CropSaleDTO saved = cropSaleManager.addCropSale(cropId, cropSaleDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update crop sale details",
            description = "Update the details of an existing crop sale",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the crop sale"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PatchMapping(BASE_PATH)
    public ResponseEntity<String> updateCropSale(@Parameter(description = "Updated crop sale details") @RequestBody CropSaleDTO cropSaleDTO) {
        cropSaleManager.updateCropSale(cropSaleDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete a crop sale",
            description = "Delete an existing crop sale by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the crop sale"),
                    @ApiResponse(responseCode = "404", description = "Crop sale not found")
            }
    )
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteById(@Parameter(description = "UUID of the crop sale to delete") @RequestParam UUID id) {
        cropSaleManager.deleteCropSale(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
