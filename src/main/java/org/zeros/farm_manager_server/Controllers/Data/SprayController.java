package org.zeros.farm_manager_server.Controllers.Data;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.SprayDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.SprayType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Interface.Data.SprayManager;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SprayController {
    public static final String BASE_PATH = "/api/user/spray";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final SprayManager sprayManager;

    @GetMapping(BASE_PATH)
    public SprayDTO getById(@RequestParam UUID id) throws NoSuchObjectException {
        Spray spray = sprayManager.getSprayById(id);
        if (spray == Spray.NONE) {
            throw new NoSuchObjectException("Machine do not exist");
        }
        return DefaultMappers.sprayMapper.entityToDto(spray);

    }

    @GetMapping(LIST_ALL_PATH)
    public Page<SprayDTO> getAll(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return sprayManager.getAllSprays(pageNumber)
                .map(DefaultMappers.sprayMapper::entityToDto);

    }

    @GetMapping(LIST_DEFAULT_PATH)
    public Page<SprayDTO> getDefault(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return sprayManager.getDefaultSprays(pageNumber)
                .map(DefaultMappers.sprayMapper::entityToDto);
    }

    @GetMapping(LIST_USER_PATH)
    public Page<SprayDTO> getUserCreated
            (@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return sprayManager.getUserSprays(pageNumber)
                .map(DefaultMappers.sprayMapper::entityToDto);

    }

    @GetMapping(LIST_PARAM_PATH)
    public Page<SprayDTO> getCriteria(@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                      @RequestParam(required = false) String name,
                                      @RequestParam(required = false) String producer,
                                      @RequestParam(required = false) SprayType sprayType,
                                      @RequestParam(required = false) String activeSubstance) {
        return sprayManager.getSpraysCriteria(name, producer, sprayType, activeSubstance, pageNumber)
                .map(DefaultMappers.sprayMapper::entityToDto);

    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNew(@RequestBody SprayDTO sprayDTO) {

        Spray saved = sprayManager.addSpray(
                DefaultMappers.sprayMapper.dtoToEntity(sprayDTO)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }

    @PatchMapping(BASE_PATH)
    ResponseEntity<String> update(@RequestBody SprayDTO sprayDTO) {
        sprayManager.updateSpray(
                DefaultMappers.sprayMapper.dtoToEntity(sprayDTO)
        );
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        sprayManager.deleteSpraySafe(sprayManager.getSprayById(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
