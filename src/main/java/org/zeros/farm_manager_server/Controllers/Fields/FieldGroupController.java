package org.zeros.farm_manager_server.Controllers.Fields;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldGroupDTO;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldGroupManager;

import java.util.Set;
import java.util.UUID;

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


    @GetMapping(ID_PATH)
    public FieldGroupDTO getFieldGroupById(@PathVariable("id") UUID id) {
        return fieldGroupManager.getFieldGroupById(id);
    }


    @GetMapping(LIST_ALL_PATH)
    public Set<FieldGroupDTO> getAllFieldGroups() {
        return fieldGroupManager.getAllFieldGroups();
    }

    @GetMapping(LIST_PARAM_PATH)
    public FieldGroupDTO getFieldGroupByName(@RequestParam String name) {
        return fieldGroupManager.getFieldGroupByName(name);
    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> createEmptyFieldGroup(@RequestParam String name
            , @RequestParam(required = false, defaultValue = "") String description) {
        FieldGroupDTO saved = fieldGroupManager.createEmptyFieldGroup(name, description);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    @PatchMapping(BASE_PATH)
    ResponseEntity<String> updateFieldGroup(@RequestBody FieldGroupDTO fieldGroupDTO) {
        fieldGroupManager.updateFieldGroup(fieldGroupDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(MOVE_FIELDS_PATH)
    ResponseEntity<String> moveFieldsToAnotherGroup(@RequestParam Set<UUID> fieldsIds, @RequestParam UUID newGroupId) {
        fieldGroupManager.moveFieldsToAnotherGroup(fieldsIds, newGroupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id, @RequestParam Boolean deleteFields) {
        if (deleteFields) {
            fieldGroupManager.deleteFieldGroupWithFields(id);
        } else {
            fieldGroupManager.deleteFieldGroupWithoutFields(id);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
