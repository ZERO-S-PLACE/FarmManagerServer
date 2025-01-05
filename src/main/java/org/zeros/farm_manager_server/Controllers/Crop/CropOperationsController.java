package org.zeros.farm_manager_server.Controllers.Crop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CropOperationsController {}
    /*
    public static final String BASE_PATH_CROP = "/api/user/crop";
    public static final String BASE_PATH_OPERATION = "/api/user/crop/operation";
    public static final String BASE_PATH_CROP_SALE = "/api/user/crop/sale";
    public static final String BASE_PATH_CROP_PARAMETERS = "/api/user/crop/parameters";
    public static final String MAIN_CROP_PATH = BASE_PATH_CROP + "/MAIN";
    public static final String INTER_CROP_PATH = BASE_PATH_CROP + "/INTER";
    private static final String CROP_PLANTS_PATH = BASE_PATH_CROP+ "/PLANTS";
    private final CropManager cropManager;
/*
    @GetMapping(LIST_PARAM_PATH)
    public Page<FarmingMachineDTO> getCriteria(@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                               @RequestParam(required = false) String model,
                                               @RequestParam(required = false) String producer,
                                               @RequestParam(required = false) OperationType operationType) {
        return farmingMachineManager.getFarmingMachineCriteria(model, producer, operationType, pageNumber)
                .map(DefaultMappers.farmingMachineMapper::entityToDto);
    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNew(@RequestBody FarmingMachineDTO farmingMachineDTO) {

        FarmingMachine saved = farmingMachineManager.addFarmingMachine(farmingMachineDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PatchMapping(BASE_PATH)
    ResponseEntity<String> update(@RequestBody FarmingMachineDTO farmingMachineDTO) {
        farmingMachineManager.updateFarmingMachine(farmingMachineDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        farmingMachineManager.deleteFarmingMachineSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/

/*
    //MainCrop createNewMainCrop(@NotNull UUID fieldPartId, @NotNull Set<UUID> cultivatedPlantsIds);
    @GetMapping(BASE_PATH_CROP)
    public CropDTO getCropById(@RequestParam UUID cropId)  {
        return DefaultMappers.cropMapper.entityToDto(cropManager.getCropById(cropId));
    }

    @PostMapping(MAIN_CROP_PATH)
    ResponseEntity<String> createNewMainCrop(@RequestParam UUID fieldParId ,
                                             @RequestParam Set<UUID> cultivatedPlantsIds) {

        MainCrop saved = cropManager.createNewMainCrop(fieldParId, cultivatedPlantsIds);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH_CROP + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }
    //InterCrop createNewInterCrop(@NotNull UUID fieldPartId, @NotNull Set<UUID> cultivatedPlantsIds);
    @PostMapping(MAIN_CROP_PATH)
    ResponseEntity<String> createNewInterCrop(@RequestParam UUID fieldParId ,
                                             @RequestParam Set<UUID> cultivatedPlantsIds) {

        InterCrop saved = cropManager.createNewInterCrop(fieldParId, cultivatedPlantsIds);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH_CROP + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

        //return null;
    }

    //Crop updateCultivatedPlants(@NotNull UUID cropId, @NotNull Set<UUID> cultivatedPlantsIds);
    @PatchMapping(CROP_PLANTS_PATH)
    ResponseEntity<String> updateCultivatedPlants(@RequestParam UUID cropId ,
                                                  @RequestParam Set<UUID> cultivatedPlantsIds) {
        cropManager.updateCultivatedPlants(cropId,cultivatedPlantsIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //Crop setDateDestroyed(@NotNull UUID interCropId, @NotNull LocalDate dateDestroyed);
    @PatchMapping(INTER_CROP_PATH)
    ResponseEntity<String> setDateDestroyed(@RequestParam UUID interCropId ,
                                                  @RequestParam LocalDate dateDestroyed) {
        cropManager.setDateDestroyed(interCropId,dateDestroyed);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //void setWorkFinished(@NotNull UUID cropId);
    @PatchMapping(BASE_PATH_CROP)
    ResponseEntity<String> setWorkFinished(@RequestParam UUID cropId) {
        cropManager.setWorkFinished(cropId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //void setFullySold(@NotNull UUID mainCropId);
    @PatchMapping(MAIN_CROP_PATH)
    ResponseEntity<String> setFullySold(@RequestParam UUID mainCropId) {
        cropManager.setFullySold(mainCropId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //void deleteCropAndItsData(@NotNull UUID cropId);
    @DeleteMapping(BASE_PATH_CROP)
    ResponseEntity<String> deleteCrop(@RequestParam UUID cropId) {
        cropManager.deleteCropAndItsData(cropId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//Seeding getSeedingById(@NotNull UUID id);

//Cultivation getCultivationById(@NotNull UUID id);

// FertilizerApplication getFertilizerApplicationById(@NotNull UUID id);

    //Seeding planSeeding(@NotNull UUID cropId, @NotNull SeedingDTO seedingDTO);

    //Seeding addSeeding(@NotNull UUID cropId, @NotNull SeedingDTO seedingDTO);



    //Cultivation planCultivation(@NotNull UUID cropId, @NotNull CultivationDTO cultivationDTO);

    //Cultivation addCultivation(@NotNull UUID cropId, @NotNull CultivationDTO cultivationDTO);




    //FertilizerApplication planFertilizerApplication(@NotNull UUID cropId, @NotNull FertilizerApplicationDTO fertilizerApplicationDTO);

    //FertilizerApplication addFertilizerApplication(@NotNull UUID cropId, @NotNull FertilizerApplicationDTO fertilizerApplicationDTO);




    //SprayApplication planSprayApplication(@NotNull UUID cropId, @NotNull SprayApplicationDTO sprayApplicationDTO);

    //SprayApplication addSprayApplication(@NotNull UUID cropId, @NotNull SprayApplicationDTO sprayApplicationDTO);

    //SprayApplication getSprayApplicationById(@NotNull UUID id);


    //Harvest planHarvest(@NotNull UUID cropId, @NotNull HarvestDTO harvestDTO);

    //Harvest addHarvest(@NotNull UUID cropId, @NotNull HarvestDTO harvestDTO);

    ///Harvest getHarvestById(@NotNull UUID id);

    //AgriculturalOperation commitPlannedOperation(@NotNull UUID operationId, @NotNull OperationType operationType);

    //AgriculturalOperation updateOperationMachine(@NotNull UUID operationId, @NotNull OperationType operationType, @NotNull UUID machineId);

    //AgriculturalOperation updateOperationParameters(@NotNull AgriculturalOperationDTO agriculturalOperationDTO);

    //void deleteOperation(@NotNull UUID operationId, @NotNull OperationType operationType);


    //void addSubside(@NotNull UUID cropId, @NotNull UUID subsideId);

    //void removeSubside(@NotNull UUID cropId, @NotNull UUID subsideId);

    //CropSale addCropSale(@NotNull UUID cropId, @NotNull CropSaleDTO cropSaleDTO);

    //CropSale updateCropSale(@NotNull CropSaleDTO cropSaleDTO);

    //void removeCropSale(@NotNull UUID cropSaleId);

    //CropSale getCropSaleById(@NotNull UUID id);



}
*/