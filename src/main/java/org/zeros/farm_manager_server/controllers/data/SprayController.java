package org.zeros.farm_manager_server.controllers.data;

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
import org.zeros.farm_manager_server.domain.dto.data.SprayDTO;
import org.zeros.farm_manager_server.domain.enums.SprayType;
import org.zeros.farm_manager_server.services.interfaces.data.SprayManager;

import java.util.UUID;

@Tag(name = "Spray Management", description = "API for managing spray records")
@Slf4j
@RequiredArgsConstructor
@RestController
public class SprayController {
    public static final String BASE_PATH = "/api/user/spray";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final SprayManager sprayManager;

    @Operation(
            summary = "Get spray by ID",
            description = "Retrieve details of a specific spray by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved spray details",
                            content = @Content(schema = @Schema(implementation = SprayDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Spray not found")
            }
    )
    @GetMapping(ID_PATH)
    public SprayDTO getById(@Parameter(description = "UUID of the spray to retrieve") @PathVariable("id") UUID id) {
        return sprayManager.getSprayById(id);
    }

    @Operation(
            summary = "Get all sprays",
            description = "Retrieve a paginated list of all sprays",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved sprays list",
                            content = @Content(schema = @Schema(implementation = SprayDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_ALL_PATH)
    public Page<SprayDTO> getAll(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return sprayManager.getAllSprays(pageNumber);
    }

    @Operation(
            summary = "Get default sprays",
            description = "Retrieve a paginated list of default sprays",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved default sprays list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_DEFAULT_PATH)
    public Page<SprayDTO> getDefault(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return sprayManager.getDefaultSprays(pageNumber);
    }

    @Operation(
            summary = "Get user-created sprays",
            description = "Retrieve a paginated list of sprays created by the user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user-created sprays list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_USER_PATH)
    public Page<SprayDTO> getUserCreated(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return sprayManager.getUserSprays(pageNumber);
    }

    @Operation(
            summary = "Get sprays by criteria",
            description = "Retrieve a paginated list of sprays based on specified criteria like name, producer, spray type, and active substance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved sprays matching the criteria"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number or criteria")
            }
    )
    @GetMapping(LIST_PARAM_PATH)
    public Page<SprayDTO> getCriteria(
            @Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Name of the spray") @RequestParam(required = false) String name,
            @Parameter(description = "Producer of the spray") @RequestParam(required = false) String producer,
            @Parameter(description = "Type of spray") @RequestParam(required = false) SprayType sprayType,
            @Parameter(description = "Active substance in the spray") @RequestParam(required = false) String activeSubstance) {

        return sprayManager.getSpraysCriteria(name, producer, sprayType, activeSubstance, pageNumber);
    }

    @Operation(
            summary = "Add a new spray",
            description = "Create a new spray by providing its details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created a new spray"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PostMapping(BASE_PATH)
    public ResponseEntity<String> addNew(@RequestBody @Parameter(description = "Details of the new spray") SprayDTO sprayDTO) {

        SprayDTO saved = sprayManager.addSpray(sprayDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update spray details",
            description = "Update the details of an existing spray",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the spray"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PatchMapping(BASE_PATH)
    public ResponseEntity<String> update(@RequestBody @Parameter(description = "Updated spray details") SprayDTO sprayDTO) {
        sprayManager.updateSpray(sprayDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete a spray",
            description = "Delete an existing spray by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the spray"),
                    @ApiResponse(responseCode = "404", description = "Spray not found")
            }
    )
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteById(@Parameter(description = "UUID of the spray to delete") @RequestParam UUID id) {
        sprayManager.deleteSpraySafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
