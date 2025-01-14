package org.zeros.farm_manager_server.Controllers.Fields;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldPartDTO;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

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


    @GetMapping(ID_PATH)
    public FieldPartDTO getByFieldId(@PathVariable("id") UUID id) {
        return fieldPartManager.getFieldPartById(id);
    }


    @GetMapping(LIST_ALL_PATH)
    public Set<FieldPartDTO> getAllFieldParts(@RequestParam UUID fieldId) {
        return fieldPartManager.getAllFieldParts(fieldId);
    }

    @GetMapping(LIST_NON_ARCHIVED_PATH)
    public Set<FieldPartDTO> getAllNonArchivedFieldParts(@RequestParam UUID fieldId) {
        return fieldPartManager.getAllNonArchivedFieldParts(fieldId);
    }


    @PatchMapping(BASE_PATH)
    ResponseEntity<String> updateFieldPartName(@RequestParam UUID fieldPartId, @RequestParam String newName) {
        fieldPartManager.updateFieldPartName(fieldPartId, newName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(RESIZE_PATH)
    ResponseEntity<String> updateFieldPartAreaTransfer(@RequestParam UUID changedPartId,
                                                       @RequestParam(required = false) UUID resizedPartId,
                                                       @RequestParam BigDecimal newArea) {
        if (resizedPartId == null) {
            fieldPartManager.updateFieldPartAreaResizeField(changedPartId, newArea);
        } else {
            fieldPartManager.updateFieldPartAreaTransfer(changedPartId, resizedPartId, newArea);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(DIVIDE_PATH)
    ResponseEntity<String> divideFieldPart(@RequestParam UUID originPartId,
                                           @RequestBody FieldPartDTO part1DTO,
                                           @RequestBody FieldPartDTO part2DTO) {
        fieldPartManager.divideFieldPart(originPartId, part1DTO, part2DTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(MERGE_PATH)
    ResponseEntity<String> mergeFieldParts(@RequestParam Set<UUID> fieldPartsIds) {
        fieldPartManager.mergeFieldParts(fieldPartsIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
