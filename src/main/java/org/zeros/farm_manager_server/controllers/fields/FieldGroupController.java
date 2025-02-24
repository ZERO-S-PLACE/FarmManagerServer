package org.zeros.farm_manager_server.controllers.fields;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.domain.dto.fields.FieldGroupDTO;
import org.zeros.farm_manager_server.services.interfaces.fields.FieldGroupManager;

import java.util.Set;
import java.util.UUID;

@Tag(name = "4.Field Group Management", description = "API for managing field groups")
@Slf4j
@RequiredArgsConstructor
@RestController
public class FieldGroupController {
    public static final String BASE_PATH = "/api/user/field/group";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    public static final String MOVE_FIELDS_PATH = BASE_PATH + "/MOVE";
    private final FieldGroupManager fieldGroupManager;

    @Operation(
            summary = "Get a field group by ID",
            description = "Retrieve a specific field group using its unique ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the field group",
                            content = @Content(schema = @Schema(implementation = FieldGroupDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Field group not found")
            }
    )
    @GetMapping(ID_PATH)
    public FieldGroupDTO getFieldGroupById(@Parameter(description = "UUID of the field group to retrieve") @PathVariable("id") UUID id) {
        return fieldGroupManager.getFieldGroupById(id);
    }

    @Operation(
            summary = "Get all field groups",
            description = "Retrieve a list of all field groups",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved all field groups",
                            content = @Content(schema = @Schema(implementation = FieldGroupDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @GetMapping(LIST_ALL_PATH)
    public Set<FieldGroupDTO> getAllFieldGroups() {
        return fieldGroupManager.getAllFieldGroups();
    }

    @Operation(
            summary = "Get a field group by name",
            description = "Retrieve a specific field group by its name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the field group by name",
                            content = @Content(schema = @Schema(implementation = FieldGroupDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Field group not found")
            }
    )
    @GetMapping(LIST_PARAM_PATH)
    public FieldGroupDTO getFieldGroupByName(@Parameter(description = "Name of the field group to retrieve") @RequestParam String name) {
        return fieldGroupManager.getFieldGroupByName(name);
    }

    @Operation(
            summary = "Create a new empty field group",
            description = "Create a new empty field group with an optional description",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created the new field group"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PostMapping(BASE_PATH)
    public ResponseEntity<String> createEmptyFieldGroup(
            @Parameter(description = "Name of the new field group") @RequestParam String name,
            @Parameter(description = "Optional description of the field group") @RequestParam(required = false, defaultValue = "") String description) {
        FieldGroupDTO saved = fieldGroupManager.createEmptyFieldGroup(name, description);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update an existing field group",
            description = "Update the details of an existing field group",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the field group"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PatchMapping(BASE_PATH)
    public ResponseEntity<String> updateFieldGroup(@Parameter(description = "Updated field group details") @RequestBody FieldGroupDTO fieldGroupDTO) {
        fieldGroupManager.updateFieldGroup(fieldGroupDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Move fields to another group",
            description = "Move a set of fields to a new field group",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully moved the fields"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data or field group ID")
            }
    )
    @PatchMapping(MOVE_FIELDS_PATH)
    public ResponseEntity<String> moveFieldsToAnotherGroup(
            @Parameter(description = "Set of field IDs to move") @RequestParam Set<UUID> fieldsIds,
            @Parameter(description = "UUID of the new group to move fields to") @RequestParam UUID newGroupId) {
        fieldGroupManager.moveFieldsToAnotherGroup(fieldsIds, newGroupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete a field group",
            description = "Delete a field group, with an option to delete or retain its fields",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the field group"),
                    @ApiResponse(responseCode = "404", description = "Field group not found")
            }
    )
    @DeleteMapping(BASE_PATH)
    public ResponseEntity<String> deleteById(
            @Parameter(description = "UUID of the field group to delete") @RequestParam UUID id,
            @Parameter(description = "Flag to delete associated fields (true/false)") @RequestParam Boolean deleteFields) {
        if (deleteFields) {
            fieldGroupManager.deleteFieldGroupWithFields(id);
        } else {
            fieldGroupManager.deleteFieldGroupWithoutFields(id);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
