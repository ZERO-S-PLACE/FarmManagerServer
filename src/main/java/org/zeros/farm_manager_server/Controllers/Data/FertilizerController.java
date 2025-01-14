package org.zeros.farm_manager_server.Controllers.Data;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FertilizerController {
    public static final String BASE_PATH = "/api/user/fertilizer";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final FertilizerManager fertilizerManager;

    @GetMapping(ID_PATH)
    public FertilizerDTO getById(@PathVariable("id") UUID id) throws NoSuchObjectException {
        return fertilizerManager.getFertilizerById(id);
    }

    @GetMapping(LIST_ALL_PATH)
    public Page<FertilizerDTO> getAll(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return fertilizerManager.getAllFertilizers(pageNumber);

    }

    @GetMapping(LIST_DEFAULT_PATH)
    public Page<FertilizerDTO> getDefault(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return fertilizerManager.getDefaultFertilizers(pageNumber);
    }

    @GetMapping(LIST_USER_PATH)
    public Page<FertilizerDTO> getUserCreated
            (@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return fertilizerManager.getUserFertilizers(pageNumber);

    }

    @GetMapping(LIST_PARAM_PATH)
    public Page<FertilizerDTO> getCriteria(@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) Boolean isNatural
    ) {
        return fertilizerManager.getFertilizersCriteria(name, isNatural, pageNumber);

    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNew(@RequestBody FertilizerDTO fertilizerDTO) {

        FertilizerDTO saved = fertilizerManager.addFertilizer(fertilizerDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }

    @PatchMapping(BASE_PATH)
    ResponseEntity<String> update(@RequestBody FertilizerDTO fertilizerDTO) {
        fertilizerManager.updateFertilizer(fertilizerDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        fertilizerManager.deleteFertilizerSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
