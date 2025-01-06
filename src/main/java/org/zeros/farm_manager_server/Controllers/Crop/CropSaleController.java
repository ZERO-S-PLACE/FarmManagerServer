package org.zeros.farm_manager_server.Controllers.Crop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropSaleManager;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CropSaleController {
    public static final String BASE_PATH = "/api/user/crop/sale";
    public static final String ID_PATH = BASE_PATH + "{id}";

    private final CropSaleManager cropSaleManager;

    @GetMapping(ID_PATH)
    public CropSaleDTO getById(@PathVariable("id") UUID id) {
        CropSale cropSale = cropSaleManager.getCropSaleById(id);
        if (cropSale == CropSale.NONE) {
            throw new IllegalArgumentExceptionCustom(CropSale.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return DefaultMappers.cropSaleMapper.entityToDto(cropSale);
    }


    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNewCropSale(@RequestParam UUID cropId, @RequestBody CropSaleDTO cropSaleDTO) {
        CropSale saved = cropSaleManager.addCropSale(cropId, cropSaleDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    @PatchMapping(BASE_PATH)
    ResponseEntity<String> updateCropSale(@RequestBody CropSaleDTO cropSaleDTO) {
        cropSaleManager.updateCropSale(cropSaleDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        cropSaleManager.deleteCropSale(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
