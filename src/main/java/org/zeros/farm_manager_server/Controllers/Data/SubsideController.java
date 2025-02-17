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
import org.zeros.farm_manager_server.Domain.DTO.Data.SubsideDTO;
import org.zeros.farm_manager_server.Services.Interface.Data.SubsideManager;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Tag(name = "Subside Management", description = "API for managing subside records")
@Slf4j
@RequiredArgsConstructor
@RestController
public class SubsideController {
    public static final String BASE_PATH = "/api/user/subside";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final SubsideManager subsideManager;

    @Operation(
            summary = "Get subside by ID",
            description = "Retrieve details of a specific subside by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved subside details",
                            content = @Content(schema = @Schema(implementation = SubsideDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Subside not found")
            }
    )
    @GetMapping(ID_PATH)
    public SubsideDTO getById(@Parameter(description = "UUID of the subside to retrieve") @PathVariable("id") UUID id) throws NoSuchObjectException {
        return subsideManager.getSubsideById(id);
    }

    @Operation(
            summary = "Get all subsides",
            description = "Retrieve a paginated list of all subsides",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved subsides list",
                            content = @Content(schema = @Schema(implementation = SubsideDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_ALL_PATH)
    public Page<SubsideDTO> getAll(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return subsideManager.getAllSubsides(pageNumber);
    }

    @Operation(
            summary = "Get default subsides",
            description = "Retrieve a paginated list of default subsides",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved default subsides list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_DEFAULT_PATH)
    public Page<SubsideDTO> getDefault(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return subsideManager.getDefaultSubsides(pageNumber);
    }

    @Operation(
            summary = "Get user-created subsides",
            description = "Retrieve a paginated list of subsides created by the user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user-created subsides list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_USER_PATH)
    public Page<SubsideDTO> getUserCreated(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return subsideManager.getUserSubsides(pageNumber);
    }

    @Operation(
            summary = "Get subsides by criteria",
            description = "Retrieve a paginated list of subsides based on specified criteria like name and species ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved subsides matching the criteria"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number or criteria")
            }
    )
    @GetMapping(LIST_PARAM_PATH)
    public Page<SubsideDTO> getCriteria(
            @Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Name of the subside") @RequestParam(required = false) String name,
            @Parameter(description = "Species ID related to the subside") @RequestParam(required = false) UUID speciesId) {
        return subsideManager.getSubsidesCriteria(name, speciesId, pageNumber);
    }

    @Operation(
            summary = "Add a new subside",
            description = "Create a new subside by providing its details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created a new subside"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PostMapping(BASE_PATH)
    public ResponseEntity<String> addNew(@RequestBody @Parameter(description = "Details of the new subside") SubsideDTO subsideDTO) {
        SubsideDTO saved = subsideManager.addSubside(subsideDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update subside details",
            description = "Update the details of an existing subside",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the subside"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PatchMapping(BASE_PATH)
    public ResponseEntity<String> update(@RequestBody @Parameter(description = "Updated subside details") SubsideDTO subsideDTO) {
        subsideManager.updateSubside(subsideDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete a subside",
            description = "Delete an existing subside by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the subside"),
                    @ApiResponse(responseCode = "404", description = "Subside not found")
            }
    )
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteById(@Parameter(description = "UUID of the subside to delete") @RequestParam UUID id) {
        subsideManager.deleteSubsideSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
