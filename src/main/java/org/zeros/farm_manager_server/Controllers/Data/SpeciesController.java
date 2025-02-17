package org.zeros.farm_manager_server.Controllers.Data;

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
import org.zeros.farm_manager_server.Domain.DTO.Data.SpeciesDTO;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;

import java.util.UUID;

@Tag(name = "Species Management", description = "API for managing species records")
@Slf4j
@RequiredArgsConstructor
@RestController
public class SpeciesController {
    public static final String BASE_PATH = "/api/user/species";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final SpeciesManager speciesManager;

    @Operation(
            summary = "Get species by ID",
            description = "Retrieve details of a specific species by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved species details",
                            content = @Content(schema = @Schema(implementation = SpeciesDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Species not found")
            }
    )
    @GetMapping(ID_PATH)
    public SpeciesDTO getById(@Parameter(description = "UUID of the species to retrieve") @PathVariable("id") UUID id) {
        return speciesManager.getSpeciesById(id);
    }

    @Operation(
            summary = "Get all species",
            description = "Retrieve a paginated list of all species",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved species list",
                            content = @Content(schema = @Schema(implementation = SpeciesDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_ALL_PATH)
    public Page<SpeciesDTO> getAll(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return speciesManager.getAllSpecies(pageNumber);
    }

    @Operation(
            summary = "Get default species",
            description = "Retrieve a paginated list of default species",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved default species list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_DEFAULT_PATH)
    public Page<SpeciesDTO> getDefault(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return speciesManager.getDefaultSpecies(pageNumber);
    }

    @Operation(
            summary = "Get user-created species",
            description = "Retrieve a paginated list of species created by the user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user-created species list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_USER_PATH)
    public Page<SpeciesDTO> getUserCreated(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return speciesManager.getUserSpecies(pageNumber);
    }

    @Operation(
            summary = "Get species by criteria",
            description = "Retrieve a paginated list of species based on specified criteria like name and family",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved species matching the criteria"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number or criteria")
            }
    )
    @GetMapping(LIST_PARAM_PATH)
    public Page<SpeciesDTO> getCriteria(
            @Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Name of the species") @RequestParam(required = false) String name,
            @Parameter(description = "Family of the species") @RequestParam(required = false) String family) {
        return speciesManager.getSpeciesCriteria(name, family, pageNumber);
    }

    @Operation(
            summary = "Add a new species",
            description = "Create a new species by providing its details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created a new species"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PostMapping(BASE_PATH)
    public ResponseEntity<String> addNew(@RequestBody @Parameter(description = "Details of the new species") SpeciesDTO speciesDTO) {
        SpeciesDTO saved = speciesManager.addSpecies(speciesDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update species details",
            description = "Update the details of an existing species",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the species"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PatchMapping(BASE_PATH)
    public ResponseEntity<String> update(@RequestBody @Parameter(description = "Updated species details") SpeciesDTO speciesDTO) {
        speciesManager.updateSpecies(speciesDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete a species",
            description = "Delete an existing species by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the species"),
                    @ApiResponse(responseCode = "404", description = "Species not found")
            }
    )
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteById(@Parameter(description = "UUID of the species to delete") @RequestParam UUID id) {
        speciesManager.deleteSpeciesSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
