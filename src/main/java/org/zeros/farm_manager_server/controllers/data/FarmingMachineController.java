package org.zeros.farm_manager_server.controllers.data;

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
import org.zeros.farm_manager_server.domain.dto.data.FarmingMachineDTO;
import org.zeros.farm_manager_server.domain.enums.OperationType;
import org.zeros.farm_manager_server.services.interfaces.data.FarmingMachineManager;

import java.util.UUID;

@Tag(name = "Farming Machine Management", description = "API for managing farming machine records")
@Slf4j
@RequiredArgsConstructor
@RestController
public class FarmingMachineController {
    public static final String BASE_PATH = "/api/user/farming_machine";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final FarmingMachineManager farmingMachineManager;

    @Operation(
            summary = "Get farming machine by ID",
            description = "Retrieve details of a specific farming machine by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved farming machine details",
                            content = @Content(schema = @Schema(implementation = FarmingMachineDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Farming machine not found")
            }
    )
    @GetMapping(ID_PATH)
    public FarmingMachineDTO getById(@Parameter(description = "UUID of the farming machine to retrieve") @PathVariable("id") UUID id) {
        return farmingMachineManager.getFarmingMachineById(id);
    }

    @Operation(
            summary = "Get all farming machines",
            description = "Retrieve a paginated list of all farming machines",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved farming machine list",
                            content = @Content(schema = @Schema(implementation = FarmingMachineDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_ALL_PATH)
    public Page<FarmingMachineDTO> getAll(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return farmingMachineManager.getAllFarmingMachines(pageNumber);
    }

    @Operation(
            summary = "Get default farming machines",
            description = "Retrieve a paginated list of default farming machines",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved default farming machine list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_DEFAULT_PATH)
    public Page<FarmingMachineDTO> getDefault(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return farmingMachineManager.getDefaultFarmingMachines(pageNumber);
    }

    @Operation(
            summary = "Get user created farming machines",
            description = "Retrieve a paginated list of farming machines created by the user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user-created farming machine list"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(LIST_USER_PATH)
    public Page<FarmingMachineDTO> getUserCreated(@Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return farmingMachineManager.getUserFarmingMachines(pageNumber);
    }

    @Operation(
            summary = "Get farming machines by criteria",
            description = "Retrieve a paginated list of farming machines based on specified criteria like model, producer, or operation type",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved farming machines matching the criteria"),
                    @ApiResponse(responseCode = "400", description = "Invalid page number or criteria")
            }
    )
    @GetMapping(LIST_PARAM_PATH)
    public Page<FarmingMachineDTO> getCriteria(
            @Parameter(description = "Page number for pagination") @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Model of the farming machine") @RequestParam(required = false) String model,
            @Parameter(description = "Producer of the farming machine") @RequestParam(required = false) String producer,
            @Parameter(description = "Operation type of the farming machine") @RequestParam(required = false) OperationType operationType) {

        return farmingMachineManager.getFarmingMachineCriteria(model, producer, operationType, pageNumber);
    }

    @Operation(
            summary = "Add a new farming machine",
            description = "Create a new farming machine by providing details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created a new farming machine"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PostMapping(BASE_PATH)
    public ResponseEntity<String> addNew(@RequestBody @Parameter(description = "Details of the new farming machine") FarmingMachineDTO farmingMachineDTO) {
        FarmingMachineDTO saved = farmingMachineManager.addFarmingMachine(farmingMachineDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update farming machine details",
            description = "Update the details of an existing farming machine",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the farming machine"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PatchMapping(BASE_PATH)
    public ResponseEntity<String> update(@RequestBody @Parameter(description = "Updated farming machine details") FarmingMachineDTO farmingMachineDTO) {
        farmingMachineManager.updateFarmingMachine(farmingMachineDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete a farming machine",
            description = "Delete an existing farming machine by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the farming machine"),
                    @ApiResponse(responseCode = "404", description = "Farming machine not found")
            }
    )
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteById(@Parameter(description = "UUID of the farming machine to delete") @RequestParam UUID id) {
        farmingMachineManager.deleteFarmingMachineSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
