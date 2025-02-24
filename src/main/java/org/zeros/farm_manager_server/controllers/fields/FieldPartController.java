package org.zeros.farm_manager_server.controllers.fields;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.domain.dto.fields.FieldPartDTO;
import org.zeros.farm_manager_server.services.interfaces.fields.FieldPartManager;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Tag(name = "5.Field Part Management", description = "API for managing field parts within fields")
@Slf4j
@RequiredArgsConstructor
@RestController
public class FieldPartController {
    public static final String BASE_PATH = "/api/user/field/part";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_NON_ARCHIVED_PATH = BASE_PATH + "/NON_ARCHIVED";
    public static final String RESIZE_PATH = BASE_PATH + "/RESIZE";
    public static final String DIVIDE_PATH = BASE_PATH + "/DIVIDE";
    public static final String MERGE_PATH = BASE_PATH + "/MERGE";
    private final FieldPartManager fieldPartManager;

    @Operation(
            summary = "Get a field part by ID",
            description = "Retrieve a specific field part using its unique ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the field part",
                            content = @Content(schema = @Schema(implementation = FieldPartDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Field part not found")
            }
    )
    @GetMapping(ID_PATH)
    public FieldPartDTO getByFieldId(@Parameter(description = "UUID of the field part to retrieve") @PathVariable("id") UUID id) {
        return fieldPartManager.getFieldPartById(id);
    }

    @Operation(
            summary = "Get all field parts of a specific field",
            description = "Retrieve a list of all field parts for a given field",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved all field parts",
                            content = @Content(schema = @Schema(implementation = FieldPartDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid field ID")
            }
    )
    @GetMapping(LIST_ALL_PATH)
    public Set<FieldPartDTO> getAllFieldParts(
            @Parameter(description = "UUID of the field to retrieve parts for") @RequestParam UUID fieldId) {
        return fieldPartManager.getAllFieldParts(fieldId);
    }

    @Operation(
            summary = "Get non-archived field parts of a specific field",
            description = "Retrieve a list of non-archived field parts for a given field",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved non-archived field parts",
                            content = @Content(schema = @Schema(implementation = FieldPartDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid field ID")
            }
    )
    @GetMapping(LIST_NON_ARCHIVED_PATH)
    public Set<FieldPartDTO> getAllNonArchivedFieldParts(
            @Parameter(description = "UUID of the field to retrieve non-archived parts for") @RequestParam UUID fieldId) {
        return fieldPartManager.getAllNonArchivedFieldParts(fieldId);
    }

    @Operation(
            summary = "Update the name of a field part",
            description = "Update the name of an existing field part",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the field part name"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PatchMapping(BASE_PATH)
    public ResponseEntity<String> updateFieldPartName(
            @Parameter(description = "UUID of the field part to update") @RequestParam UUID fieldPartId,
            @Parameter(description = "New name for the field part") @RequestParam String newName) {
        fieldPartManager.updateFieldPartName(fieldPartId, newName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Resize or transfer the area of a field part",
            description = "Resize a field part or transfer a portion of its area to another field part",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully resized or transferred area"),
                    @ApiResponse(responseCode = "400", description = "Invalid data for resizing or transferring area")
            }
    )
    @PatchMapping(RESIZE_PATH)
    public ResponseEntity<String> updateFieldPartAreaTransfer(
            @Parameter(description = "UUID of the field part to resize or transfer") @RequestParam UUID changedPartId,
            @Parameter(description = "Optional UUID of another field part to transfer area to") @RequestParam(required = false) UUID resizedPartId,
            @Parameter(description = "New area of the field part") @RequestParam BigDecimal newArea) {
        if (resizedPartId == null) {
            fieldPartManager.updateFieldPartAreaResizeField(changedPartId, newArea);
        } else {
            fieldPartManager.updateFieldPartAreaTransfer(changedPartId, resizedPartId, newArea);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Divide a field part into two parts",
            description = "Divide a field part into two smaller field parts",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully divided the field part"),
                    @ApiResponse(responseCode = "400", description = "Invalid data for dividing the field part")
            }
    )
    @PatchMapping(DIVIDE_PATH)
    public ResponseEntity<String> divideFieldPart(
            @Parameter(description = "UUID of the field part to divide") @RequestParam UUID originPartId,
            @Parameter(description = "DTO for the first new field part") @RequestBody FieldPartDTO part1DTO,
            @Parameter(description = "DTO for the second new field part") @RequestBody FieldPartDTO part2DTO) {
        fieldPartManager.divideFieldPart(originPartId, part1DTO, part2DTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Merge multiple field parts into one",
            description = "Merge several field parts into one larger field part",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully merged the field parts"),
                    @ApiResponse(responseCode = "400", description = "Invalid data for merging the field parts")
            }
    )
    @PatchMapping(MERGE_PATH)
    public ResponseEntity<String> mergeFieldParts(
            @Parameter(description = "Set of UUIDs of field parts to merge") @RequestParam Set<UUID> fieldPartsIds) {
        fieldPartManager.mergeFieldParts(fieldPartsIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
