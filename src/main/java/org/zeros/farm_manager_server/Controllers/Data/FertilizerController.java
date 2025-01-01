package org.zeros.farm_manager_server.Controllers.Data;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FertilizerController {
    public static final String BASE_PATH = "/api/user/fertilizer";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final FertilizerManager fertilizerManager;

    @GetMapping(BASE_PATH)
    public FertilizerDTO getById(@RequestParam UUID id) throws NoSuchObjectException {
        Fertilizer fertilizer = fertilizerManager.getFertilizerById(id);
        if (fertilizer == Fertilizer.NONE) {
            throw new NoSuchObjectException("Machine do not exist");
        }
        return DefaultMappers.fertilizerMapper.entityToDto(fertilizer);

    }

    @GetMapping(LIST_ALL_PATH)
    public Page<FertilizerDTO> getAll(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return fertilizerManager.getAllFertilizers(pageNumber)
                .map(DefaultMappers.fertilizerMapper::entityToDto);

    }

    @GetMapping(LIST_DEFAULT_PATH)
    public Page<FertilizerDTO> getDefault(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return fertilizerManager.getDefaultFertilizers(pageNumber)
                .map(DefaultMappers.fertilizerMapper::entityToDto);
    }

    @GetMapping(LIST_USER_PATH)
    public Page<FertilizerDTO> getUserCreated
            (@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return fertilizerManager.getUserFertilizers(pageNumber)
                .map(DefaultMappers.fertilizerMapper::entityToDto);

    }

    @GetMapping(LIST_PARAM_PATH)
    public Page<FertilizerDTO> getCriteria(@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                      @RequestParam(required = false) String name,
                                           @RequestParam (required = false) Boolean isNatural
    )
                                      {
        return fertilizerManager.getFertilizersCriteria(name,isNatural, pageNumber)
                .map(DefaultMappers.fertilizerMapper::entityToDto);

    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNew(@RequestBody FertilizerDTO fertilizerDTO) {

        Fertilizer saved = fertilizerManager.addFertilizer(
                DefaultMappers.fertilizerMapper.dtoToEntity(fertilizerDTO)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }

    @PatchMapping(BASE_PATH)
    ResponseEntity<String> update(@RequestBody FertilizerDTO fertilizerDTO) {
        fertilizerManager.updateFertilizer(
                DefaultMappers.fertilizerMapper.dtoToEntity(fertilizerDTO)
        );
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        fertilizerManager.deleteFertilizerSafe(fertilizerManager.getFertilizerById(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
