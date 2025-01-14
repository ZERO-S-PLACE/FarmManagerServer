package org.zeros.farm_manager_server.Controllers.Fields;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldDTO;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldManager;

import java.util.Set;
import java.util.UUID;

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
    private final LoggedUserConfiguration loggedUserConfiguration;


    @GetMapping(ID_PATH)
    public FieldDTO getByFieldId(@PathVariable("id") UUID id) {
        return fieldManager.getFieldById(id);
    }

    @GetMapping(LIST_ALL_PATH)
    public Set<FieldDTO> getAllFields() {
        return fieldManager.getAllFields();
    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> createField(@RequestParam(required = false) UUID groupId, @RequestBody FieldDTO fieldDTO) {
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


    @PatchMapping(BASE_PATH)
    ResponseEntity<String> updateField(@RequestBody FieldDTO fieldDTO) {
        fieldManager.updateField(fieldDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(ARCHIVE_PATH)
    ResponseEntity<String> archiveField(@RequestParam UUID fieldId) {
        fieldManager.archiveField(fieldId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(DE_ARCHIVE_PATH)
    ResponseEntity<String> deArchiveField(@RequestParam UUID fieldId) {
        fieldManager.archiveField(fieldId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        fieldManager.deleteFieldWithData(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
