package org.zeros.farm_manager_server.Controllers.Operations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Services.Interface.Operations.AgriculturalOperationsManager;

import java.util.UUID;

@Tag(name = "Agricultural Operations", description = "API for managing agricultural operations related to crops")
@Slf4j
@RequiredArgsConstructor
@RestController
public class AgriculturalOperationsController {
    public static final String BASE_PATH = "/api/user/crop/operation";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String PLAN_OPERATION_PATH = BASE_PATH + "/PLAN";
    public static final String ADD_OPERATION_PATH = BASE_PATH + "/ADD";
    public static final String OPERATION_MACHINE_PATH = BASE_PATH + "/UPDATE_MACHINE";
    private final AgriculturalOperationsManager operationsManager;

    @Operation(
            summary = "Get operation details by ID",
            description = "Retrieve an agricultural operation by its unique ID and operation type.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the operation",
                            content = @Content(schema = @Schema(implementation = AgriculturalOperationDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Operation not found")
            }
    )
    @GetMapping(ID_PATH)
    public AgriculturalOperationDTO getOperationById(
            @Parameter(description = "UUID of the operation to retrieve") @PathVariable("id") UUID id,
            @Parameter(description = "Type of the operation") @RequestParam OperationType type) {
        return operationsManager.getOperationById(id, type);
    }

    @Operation(
            summary = "Plan a new operation for a crop",
            description = "Plan a new agricultural operation for a specific crop.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully planned the operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PostMapping(PLAN_OPERATION_PATH)
    ResponseEntity<String> planOperation(
            @Parameter(description = "UUID of the crop for which the operation is being planned") @RequestParam UUID cropId,
            @Parameter(description = "Details of the operation to plan") @RequestBody AgriculturalOperationDTO operationDTO) {
        AgriculturalOperationDTO saved = operationsManager.planOperation(cropId, operationDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Add a new operation to an existing crop",
            description = "Add a new agricultural operation to a crop, such as planting or harvesting.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully added the operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PostMapping(ADD_OPERATION_PATH)
    ResponseEntity<String> addOperation(
            @Parameter(description = "UUID of the crop to which the operation is added") @RequestParam UUID cropId,
            @Parameter(description = "Details of the operation to add") @RequestBody AgriculturalOperationDTO operationDTO) {
        AgriculturalOperationDTO saved = operationsManager.addOperation(cropId, operationDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Mark a planned operation as performed",
            description = "Update a planned operation to indicate it has been performed.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully marked operation as performed"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PatchMapping(ADD_OPERATION_PATH)
    ResponseEntity<String> setPlannedOperationPerformed(
            @Parameter(description = "UUID of the crop to update the operation status") @RequestParam UUID cropId,
            @Parameter(description = "Type of operation to update") @RequestParam OperationType type) {
        operationsManager.setPlannedOperationPerformed(cropId, type);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Update farming machine for an operation",
            description = "Update the farming machine used for a specific agricultural operation.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the operation machine"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PatchMapping(OPERATION_MACHINE_PATH)
    ResponseEntity<String> updateOperationMachine(
            @Parameter(description = "UUID of the crop for the operation") @RequestParam UUID cropId,
            @Parameter(description = "Type of the operation") @RequestParam OperationType type,
            @Parameter(description = "UUID of the farming machine to use for the operation") @RequestParam UUID farmingMachineId) {
        operationsManager.updateOperationMachine(cropId, type, farmingMachineId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Update parameters of an operation",
            description = "Update the parameters or details of an existing agricultural operation.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the operation parameters"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PatchMapping(BASE_PATH)
    ResponseEntity<String> updateOperationParameters(@RequestBody AgriculturalOperationDTO operationDTO) {
        operationsManager.updateOperationParameters(operationDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete an agricultural operation",
            description = "Delete an agricultural operation by its ID and operation type.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(
            @Parameter(description = "UUID of the operation to delete") @RequestParam UUID id,
            @Parameter(description = "Type of operation to delete") @RequestParam OperationType type) {
        operationsManager.deleteOperation(id, type);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
