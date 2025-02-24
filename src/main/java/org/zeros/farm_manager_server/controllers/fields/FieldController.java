package org.zeros.farm_manager_server.controllers.fields;

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
import org.zeros.farm_manager_server.domain.dto.fields.FieldDTO;
import org.zeros.farm_manager_server.services.interfaces.fields.FieldManager;

import java.util.Set;
import java.util.UUID;

@Tag(name = "3.Field Management", description = "API for managing field records")
@Slf4j
@RequiredArgsConstructor
@RestController
public class FieldController {
    public static final String BASE_PATH = "/api/user/field";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String ARCHIVE_PATH = BASE_PATH + "/ARCHIVE";
    public static final String DE_ARCHIVE_PATH = BASE_PATH + "/DE_ARCHIVE";
    private final FieldManager fieldManager;


    @Operation(
            summary = "Get field by ID",
            description = "Retrieve details of a specific field by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved field details",
                            content = @Content(schema = @Schema(implementation = FieldDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Field not found")
            }
    )
    @GetMapping(ID_PATH)
    public FieldDTO getByFieldId(@Parameter(description = "UUID of the field to retrieve") @PathVariable("id") UUID id) {
        return fieldManager.getFieldById(id);
    }

    @Operation(
            summary = "Get all fields",
            description = "Retrieve a set of all fields",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved fields list",
                            content = @Content(schema = @Schema(implementation = FieldDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @GetMapping(LIST_ALL_PATH)
    public Set<FieldDTO> getAllFields() {
        return fieldManager.getAllFields();
    }

    @Operation(
            summary = "Create a new field",
            description = "Create a new field, optionally in a specific group",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created a new field"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PostMapping(BASE_PATH)
    public ResponseEntity<String> createField(
            @Parameter(description = "UUID of the group to assign the field to (optional)") @RequestParam(required = false) UUID groupId,
            @Parameter(description = "Details of the field to create") @RequestBody FieldDTO fieldDTO) {
        FieldDTO saved;
        if (groupId == null) {
            saved = fieldManager.createFieldDefault(fieldDTO);
        } else {
            saved = fieldManager.createFieldInGroup(fieldDTO, groupId);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update an existing field",
            description = "Update the details of an existing field",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the field"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data provided")
            }
    )
    @PatchMapping(BASE_PATH)
    public ResponseEntity<String> updateField(@Parameter(description = "Updated field details") @RequestBody FieldDTO fieldDTO) {
        fieldManager.updateField(fieldDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Archive a field",
            description = "Mark a field as archived",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully archived the field"),
                    @ApiResponse(responseCode = "400", description = "Field ID not provided or invalid")
            }
    )
    @PatchMapping(ARCHIVE_PATH)
    public ResponseEntity<String> archiveField(@Parameter(description = "UUID of the field to archive") @RequestParam UUID fieldId) {
        fieldManager.archiveField(fieldId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "De-archive a field",
            description = "Restore an archived field back to its active state",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully de-archived the field"),
                    @ApiResponse(responseCode = "400", description = "Field ID not provided or invalid")
            }
    )
    @PatchMapping(DE_ARCHIVE_PATH)
    public ResponseEntity<String> deArchiveField(@Parameter(description = "UUID of the field to de-archive") @RequestParam UUID fieldId) {
        fieldManager.deArchiveField(fieldId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete a field",
            description = "Delete a field and all its associated data",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the field"),
                    @ApiResponse(responseCode = "404", description = "Field not found")
            }
    )
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteById(@Parameter(description = "UUID of the field to delete") @RequestParam UUID id) {
        fieldManager.deleteFieldWithData(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
