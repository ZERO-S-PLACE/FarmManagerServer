package org.zeros.farm_manager_server.Controllers.Data;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Tag(name = "Fertilizer Management", description = "API for managing fertilizer records")
@Slf4j
@RequiredArgsConstructor
@RestController
public class FertilizerController {
    public static final String BASE_PATH = "/api/user/fertilizer";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final FertilizerManager fertilizerManager;

    @Operation(
            summary = "Get fertilizer by ID",
            description = "Retrieve details of a specific fertilizer by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved fertilizer details",
                            content = @Content(schema = @Schema(implementation = FertilizerDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Fertilizer not found")
            }
    )
    @GetMapping(ID_PATH)
    public FertilizerDTO getById(@Parameter(description = "UUID of the fertilizer to retrieve") @PathVariable("id") UUID id) throws NoSuchObjectException {
        return fertilizerManager.getFertilizerById(id);
    }

    @Operation(
            summary = "Get all fertilizers",
            description = "Retrieve a paginated list of all fertilizers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved fertilizer list",
                            content = @Content(schema = @Schema(implementation = FertilizerDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_ALL_PATH)
    public Page<FertilizerDTO> getAll(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return fertilizerManager.getAllFertilizers(pageNumber);
    }

    @Operation(
            summary = "Get default fertilizers",
            description = "Retrieve a paginated list of default fertilizers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved default fertilizer list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_DEFAULT_PATH)
    public Page<FertilizerDTO> getDefault(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return fertilizerManager.getDefaultFertilizers(pageNumber);
    }

    @Operation(
            summary = "Get user-created fertilizers",
            description = "Retrieve a paginated list of fertilizers created by the user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user-created fertilizer list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_USER_PATH)
    public Page<FertilizerDTO> getUserCreated(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return fertilizerManager.getUserFertilizers(pageNumber);
    }

    @Operation(
            summary = "Get fertilizers by criteria",
            description = "Retrieve a paginated list of fertilizers based on specified criteria like name and natural status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved fertilizers matching the criteria"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number or criteria")
            }
    )
    @GetMapping(LIST_PARAM_PATH)
    public Page<FertilizerDTO> getCriteria(
            @Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Name of the fertilizer") @RequestParam(required = false) String name,
            @Parameter(description = "Natural status of the fertilizer") @RequestParam(required = false) Boolean isNatural) {

        return fertilizerManager.getFertilizersCriteria(name, isNatural, pageNumber);
    }

    @Operation(
            summary = "Add a new fertilizer",
            description = "Create a new fertilizer by providing details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created a new fertilizer"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PostMapping(BASE_PATH)
    public ResponseEntity<String> addNew(@RequestBody @Parameter(description = "Details of the new fertilizer") FertilizerDTO fertilizerDTO) {
        FertilizerDTO saved = fertilizerManager.addFertilizer(fertilizerDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update fertilizer details",
            description = "Update the details of an existing fertilizer",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the fertilizer"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PatchMapping(BASE_PATH)
    public ResponseEntity<String> update(@RequestBody @Parameter(description = "Updated fertilizer details") FertilizerDTO fertilizerDTO) {
        fertilizerManager.updateFertilizer(fertilizerDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete a fertilizer",
            description = "Delete an existing fertilizer by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the fertilizer"),
                    @ApiResponse(responseCode = "404", description = "Fertilizer not found")
            }
    )
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteById(@Parameter(description = "UUID of the fertilizer to delete") @RequestParam UUID id) {
        fertilizerManager.deleteFertilizerSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
