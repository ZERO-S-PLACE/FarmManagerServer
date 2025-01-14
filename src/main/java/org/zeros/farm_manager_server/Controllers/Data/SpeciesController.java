package org.zeros.farm_manager_server.Controllers.Data;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Data.SpeciesDTO;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SpeciesController {
    public static final String BASE_PATH = "/api/user/species";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final SpeciesManager speciesManager;

    @GetMapping(ID_PATH)
    public SpeciesDTO getById(@PathVariable("id") UUID id) {
        return speciesManager.getSpeciesById(id);
    }

    @GetMapping(LIST_ALL_PATH)
    public Page<SpeciesDTO> getAll(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return speciesManager.getAllSpecies(pageNumber);
    }

    @GetMapping(LIST_DEFAULT_PATH)
    public Page<SpeciesDTO> getDefault(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return speciesManager.getDefaultSpecies(pageNumber);
    }

    @GetMapping(LIST_USER_PATH)
    public Page<SpeciesDTO> getUserCreated
            (@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return speciesManager.getUserSpecies(pageNumber);
    }

    @GetMapping(LIST_PARAM_PATH)
    public Page<SpeciesDTO> getCriteria(@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String family) {
        return speciesManager.getSpeciesCriteria(name, family, pageNumber);
    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNew(@RequestBody SpeciesDTO speciesDTO) {
        SpeciesDTO saved = speciesManager.addSpecies(speciesDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PatchMapping(BASE_PATH)
    ResponseEntity<String> update(@RequestBody SpeciesDTO speciesDTO) {
        speciesManager.updateSpecies(speciesDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        speciesManager.deleteSpeciesSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
