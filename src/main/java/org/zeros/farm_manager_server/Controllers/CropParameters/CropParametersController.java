package org.zeros.farm_manager_server.Controllers.CropParameters;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Interface.CropParameters.CropParametersManager;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CropParametersController {
    public static final String BASE_PATH = "/api/user/crop/parameters";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final CropParametersManager cropParametersManager;

    @GetMapping(BASE_PATH)
    public CropParametersDTO getById(@RequestParam UUID id) throws NoSuchObjectException {
        CropParameters cropParameters = cropParametersManager.getCropParametersById(id);
        if (cropParameters == CropParameters.NONE) {
            throw new IllegalArgumentExceptionCustom(CropParameters.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return DefaultMappers.cropParametersMapper.entityToDto(cropParameters);

    }

    @GetMapping(LIST_ALL_PATH)
    public Page<CropParametersDTO> getAll(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return cropParametersManager.getAllCropParameters(pageNumber).map(DefaultMappers.cropParametersMapper::entityToDto);

    }

    @GetMapping(LIST_PARAM_PATH)
    public Page<CropParametersDTO> getCriteria(@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                               @RequestParam(required = false) String name,
                                               @RequestParam(required = false) ResourceType resourceType) {
        return cropParametersManager.getCropParametersCriteria(name, resourceType, pageNumber).map(DefaultMappers.cropParametersMapper::entityToDto);
    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNew(@RequestBody CropParametersDTO cropParametersDTO) {
        CropParameters saved = cropParametersManager.addCropParameters(cropParametersDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PatchMapping(BASE_PATH)
    <T extends CropParametersDTO> ResponseEntity<String> update(@RequestBody T cropParametersDTO) {
        cropParametersManager.updateCropParameters(cropParametersDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        cropParametersManager.deleteCropParametersSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}