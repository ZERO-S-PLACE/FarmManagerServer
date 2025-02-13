package org.zeros.farm_manager_server.Controllers.Data;


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

    @GetMapping(ID_PATH)
    public SubsideDTO getById(@PathVariable("id") UUID id) throws NoSuchObjectException {
        return subsideManager.getSubsideById(id);
    }

    @GetMapping(LIST_ALL_PATH)
    public Page<SubsideDTO> getAll(@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return subsideManager.getAllSubsides(pageNumber);

    }

    @GetMapping(LIST_DEFAULT_PATH)
    public Page<SubsideDTO> getDefault(@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return subsideManager.getDefaultSubsides(pageNumber);
    }

    @GetMapping(LIST_USER_PATH)
    public Page<SubsideDTO> getUserCreated(@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return subsideManager.getUserSubsides(pageNumber);

    }

    @GetMapping(LIST_PARAM_PATH)
    public Page<SubsideDTO> getCriteria(@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) UUID speciesId) {
        return subsideManager.getSubsidesCriteria(name, speciesId, pageNumber);

    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNew(@RequestBody SubsideDTO subsideDTO) {
        SubsideDTO saved = subsideManager.addSubside(subsideDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PatchMapping(BASE_PATH)
    ResponseEntity<String> update(@RequestBody SubsideDTO subsideDTO) {
        subsideManager.updateSubside(subsideDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        subsideManager.deleteSubsideSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}