package org.zeros.farm_manager_server.Controllers.Data;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Data.PlantDTO;
import org.zeros.farm_manager_server.Services.Interface.Data.PlantManager;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PlantController {
    public static final String BASE_PATH = "/api/user/plant";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final PlantManager plantManager;

    @GetMapping(ID_PATH)
    public PlantDTO getById(@PathVariable("id") UUID id) throws NoSuchObjectException {
        return plantManager.getPlantById(id);
    }

    @GetMapping(LIST_ALL_PATH)
    public Page<PlantDTO> getAll(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return plantManager.getAllPlants(pageNumber);
    }

    @GetMapping(LIST_DEFAULT_PATH)
    public Page<PlantDTO> getDefault(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return plantManager.getDefaultPlants(pageNumber);
    }

    @GetMapping(LIST_USER_PATH)
    public Page<PlantDTO> getUserCreated
            (@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return plantManager.getUserPlants(pageNumber);
    }

    @GetMapping(LIST_PARAM_PATH)
    public Page<PlantDTO> getCriteria(@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                      @RequestParam(required = false) String variety,
                                      @RequestParam(required = false) UUID speciesId) {
        return plantManager.getPlantsCriteria(variety, speciesId, pageNumber);
    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNew(@RequestBody PlantDTO plantDTO) {

        PlantDTO saved = plantManager.addPlant(plantDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }

    @PatchMapping(BASE_PATH)
    ResponseEntity<String> update(@RequestBody PlantDTO plantDTO) {
        plantManager.updatePlant(plantDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        plantManager.deletePlantSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
