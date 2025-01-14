package org.zeros.farm_manager_server.Controllers.Crop;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropParametersManager;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CropParametersController {
    public static final String BASE_PATH = "/api/user/crop/parameters";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final CropParametersManager cropParametersManager;

    @GetMapping(ID_PATH)
    public CropParametersDTO getById(@PathVariable UUID id) {
        return cropParametersManager.getCropParametersById(id);
    }

    @GetMapping(LIST_ALL_PATH)
    public Page<CropParametersDTO> getAll(@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return cropParametersManager.getAllCropParameters(pageNumber);
    }

    @GetMapping(LIST_PARAM_PATH)
    public Page<CropParametersDTO> getCriteria(@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                               @RequestParam(required = false) String name,
                                               @RequestParam(required = false) ResourceType resourceType) {
        return cropParametersManager.getCropParametersCriteria(name, resourceType, pageNumber);
    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNew(@RequestBody CropParametersDTO cropParametersDTO) {
        CropParametersDTO saved = cropParametersManager.addCropParameters(cropParametersDTO);
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