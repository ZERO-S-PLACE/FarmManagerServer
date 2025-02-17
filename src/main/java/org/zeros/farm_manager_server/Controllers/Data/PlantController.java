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
import org.zeros.farm_manager_server.Domain.DTO.Data.PlantDTO;
import org.zeros.farm_manager_server.Services.Interface.Data.PlantManager;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Tag(name = "Plant Management", description = "API for managing plant records")
@Slf4j
@RequiredArgsConstructor
@RestController
public class PlantController {
    public static final String BASE_PATH = "/api/user/plant";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final PlantManager plantManager;

    @Operation(
            summary = "Get plant by ID",
            description = "Retrieve details of a specific plant by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved plant details",
                            content = @Content(schema = @Schema(implementation = PlantDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Plant not found")
            }
    )
    @GetMapping(ID_PATH)
    public PlantDTO getById(@Parameter(description = "UUID of the plant to retrieve") @PathVariable("id") UUID id) throws NoSuchObjectException {
        return plantManager.getPlantById(id);
    }

    @Operation(
            summary = "Get all plants",
            description = "Retrieve a paginated list of all plants",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved plant list",
                            content = @Content(schema = @Schema(implementation = PlantDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_ALL_PATH)
    public Page<PlantDTO> getAll(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return plantManager.getAllPlants(pageNumber);
    }

    @Operation(
            summary = "Get default plants",
            description = "Retrieve a paginated list of default plants",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved default plant list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_DEFAULT_PATH)
    public Page<PlantDTO> getDefault(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return plantManager.getDefaultPlants(pageNumber);
    }

    @Operation(
            summary = "Get user-created plants",
            description = "Retrieve a paginated list of plants created by the user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user-created plant list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_USER_PATH)
    public Page<PlantDTO> getUserCreated(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return plantManager.getUserPlants(pageNumber);
    }

    @Operation(
            summary = "Get plants by criteria",
            description = "Retrieve a paginated list of plants based on specified criteria like variety and species ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved plants matching the criteria"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number or criteria")
            }
    )
    @GetMapping(LIST_PARAM_PATH)
    public Page<PlantDTO> getCriteria(
            @Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Variety of the plant") @RequestParam(required = false) String variety,
            @Parameter(description = "UUID of the species") @RequestParam(required = false) UUID speciesId) {
        return plantManager.getPlantsCriteria(variety, speciesId, pageNumber);
    }

    @Operation(
            summary = "Add a new plant",
            description = "Create a new plant by providing details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created a new plant"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PostMapping(BASE_PATH)
    public ResponseEntity<String> addNew(@RequestBody @Parameter(description = "Details of the new plant") PlantDTO plantDTO) {
        PlantDTO saved = plantManager.addPlant(plantDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update plant details",
            description = "Update the details of an existing plant",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the plant"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PatchMapping(BASE_PATH)
    public ResponseEntity<String> update(@RequestBody @Parameter(description = "Updated plant details") PlantDTO plantDTO) {
        plantManager.updatePlant(plantDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete a plant",
            description = "Delete an existing plant by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the plant"),
                    @ApiResponse(responseCode = "404", description = "Plant not found")
            }
    )
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteById(@Parameter(description = "UUID of the plant to delete") @RequestParam UUID id) {
        plantManager.deletePlantSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
