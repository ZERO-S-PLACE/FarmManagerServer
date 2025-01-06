package org.zeros.farm_manager_server.Controllers.Crop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.InterCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CropController {
    public static final String BASE_PATH = "/api/user/crop";
    public static final String ID_PATH = BASE_PATH+"{cropId}";
    public static final String MAIN_CROP_PATH = BASE_PATH + "/main";
    public static final String INTER_CROP_PATH = BASE_PATH + "/inter";
    public static final String WORK_FINISHED_PATH = BASE_PATH + "/SET_FINISHED";
    public static final String ADD_SUBSIDE_PATH = BASE_PATH + "/subside/ADD";
    public static final String REMOVE_SUBSIDE_PATH = BASE_PATH + "/subside/REMOVE";
    public static final String CROP_PLANTS_PATH = BASE_PATH + "/subside/plants";

    private final CropManager cropManager;

    @GetMapping(ID_PATH)
    public CropDTO getById(@PathVariable("cropId") UUID cropId) {
        Crop crop = cropManager.getCropById(cropId);
        if (crop.equals(MainCrop.NONE) || crop.equals(InterCrop.NONE)) {
            throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return DefaultMappers.cropMapper.entityToDto(crop);

    }

    @PostMapping(MAIN_CROP_PATH)
    ResponseEntity<String> createNewMainCrop(@RequestParam UUID fieldPartId, @RequestParam Set<UUID> cultivatedPlantsIds) {
        Crop saved = cropManager.createNewMainCrop(fieldPartId, cultivatedPlantsIds);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", MAIN_CROP_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    @PostMapping(INTER_CROP_PATH)
    ResponseEntity<String> createNewInterCrop(@RequestParam UUID fieldPartId, @RequestParam Set<UUID> cultivatedPlantsIds) {
        Crop saved = cropManager.createNewInterCrop(fieldPartId, cultivatedPlantsIds);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", INTER_CROP_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    @PatchMapping(CROP_PLANTS_PATH)
    ResponseEntity<String> updateCultivatedPlants(@RequestParam UUID cropId, @RequestParam Set<UUID> cultivatedPlantsIds) {
        cropManager.updateCultivatedPlants(cropId, cultivatedPlantsIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(ADD_SUBSIDE_PATH)
    ResponseEntity<String> addSubside(@RequestParam UUID cropId, @RequestParam UUID subsideId) {
        cropManager.addSubside(cropId, subsideId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(REMOVE_SUBSIDE_PATH)
    ResponseEntity<String> removeSubside(@RequestParam UUID cropId, @RequestParam UUID subsideId) {
        cropManager.removeSubside(cropId, subsideId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PatchMapping(INTER_CROP_PATH)
    ResponseEntity<String> setDateDestroyed(@RequestParam UUID interCropId, @RequestParam LocalDate dateDestroyed) {
        cropManager.setDateDestroyed(interCropId, dateDestroyed);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(WORK_FINISHED_PATH)
    ResponseEntity<String> setWorkFinished(@RequestParam UUID finishedCropId) {
        cropManager.setWorkFinished(finishedCropId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(MAIN_CROP_PATH)
    ResponseEntity<String> setFullySold(@RequestParam UUID fullySoldCropId) {
        cropManager.setFullySold(fullySoldCropId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteCrop(@RequestParam UUID cropId) {
        cropManager.deleteCropAndItsData(cropId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
