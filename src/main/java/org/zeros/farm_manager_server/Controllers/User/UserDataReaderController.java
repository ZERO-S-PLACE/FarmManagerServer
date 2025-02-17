package org.zeros.farm_manager_server.Controllers.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Services.Interface.User.UserDataReader;

import java.util.Set;

@Tag(name = "User Data Reader", description = "API for reading user-specific data like planned operations and crop statuses.")
@Slf4j
@RequiredArgsConstructor
@RestController
public class UserDataReaderController {
    public static final String BASE_PATH = "/api/user";
    public static final String PLANNED_OPERATIONS_PATH = BASE_PATH + "/operations/PLANNED";
    public static final String ACTIVE_CROPS_PATH = BASE_PATH + "/ACTIVE_CROPS";
    public static final String UNSOLD_CROPS_PATH = BASE_PATH + "/UNSOLD_CROPS";
    private final UserDataReader userDataReader;

    @Operation(
            summary = "Get all planned operations",
            description = "Retrieve all the planned agricultural operations for the user, filtered by operation type if provided.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved planned operations",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AgriculturalOperationDTO.class)))),
                    @ApiResponse(responseCode = "400", description = "Invalid operation type")
            }
    )
    @GetMapping(PLANNED_OPERATIONS_PATH)
    Set<AgriculturalOperationDTO> getAllPlannedOperations(
            @RequestParam(required = false, defaultValue = "ANY") @Parameter(description = "Filter planned operations by operation type. Defaults to 'ANY'.") OperationType operationType) {
        return userDataReader.getAllPlannedOperations(operationType);
    }

    @Operation(
            summary = "Get all active crops",
            description = "Retrieve all crops that are currently active for the user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved active crops",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CropDTO.class)))),
            }
    )
    @GetMapping(ACTIVE_CROPS_PATH)
    Set<CropDTO> getAllActiveCrops() {
        return userDataReader.getAllActiveCrops();
    }

    @Operation(
            summary = "Get all unsold crops",
            description = "Retrieve all crops that are unsold for the user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved unsold crops",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CropDTO.class)))),
            }
    )
    @GetMapping(UNSOLD_CROPS_PATH)
    Set<CropDTO> getAllUnsoldCrops() {
        return userDataReader.getAllUnsoldCrops();
    }
}
