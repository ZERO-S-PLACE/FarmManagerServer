package org.zeros.farm_manager_server.DAO.Interface;

import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crops.Crop.InterCrop;
import org.zeros.farm_manager_server.entities.Crops.Crop.MainCrop;
import org.zeros.farm_manager_server.entities.Crops.CropSale;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Subside;
import org.zeros.farm_manager_server.entities.fields.FieldPart;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface CropOperationsManager {

    MainCrop createNewMainCrop(FieldPart fieldPart, Set<Plant> cultivatedPlants);
    InterCrop createNewInterCrop(FieldPart fieldPart, Set<Plant> cultivatedPlants);
    void deleteCropAndItsData(Crop crop);
    Crop updateCultivatedPlants(Crop crop,Set<Plant> cultivatedPlants);
    Crop setDateDestroyed(InterCrop interCrop, LocalDate dateDestroyed);
    Crop setWorkFinished(MainCrop mainCrop);
    Crop setFullySold(MainCrop mainCrop, Boolean isFullySold);
    Crop getCropById(UUID id);

    AgriculturalOperation commitPlannedOperation(AgriculturalOperation agriculturalOperation);
    AgriculturalOperation updateOperationMachine(AgriculturalOperation agriculturalOperation, FarmingMachine farmingMachine);
    AgriculturalOperation updateOperationParameters(AgriculturalOperation agriculturalOperation);

    Seeding planSeeding(Crop crop, Seeding seeding);
    Seeding addSeeding(Crop crop,Seeding seeding);
    void deleteSeeding(Seeding seeding);
    Seeding getSeedingById(UUID id);


    Cultivation planCultivation(Crop crop, Cultivation cultivation);
    Cultivation addCultivation(Crop crop,Cultivation cultivation);
    void deleteCultivation(Cultivation cultivation);
    Cultivation getCultivationById(UUID id);

    FertilizerApplication planFertilizerApplication(Crop crop, FertilizerApplication fertilizerApplication);
    FertilizerApplication addFertilizerApplication(Crop crop,FertilizerApplication fertilizerApplication);
    void deleteFertilizerApplication(FertilizerApplication fertilizerApplication);
    FertilizerApplication getFertilizerApplicationById(UUID id);

    SprayApplication planSprayApplication(Crop crop, SprayApplication sprayApplication);
    SprayApplication addSprayApplication(Crop crop,SprayApplication sprayApplication);
    void deleteSprayApplication(SprayApplication sprayApplication);
    SprayApplication getSprayApplicationById(UUID id);

    Harvest planHarvest(MainCrop crop, Harvest harvest);
    Harvest addHarvest(MainCrop crop,Harvest harvest);
    void deleteHarvest(Harvest harvest);
    Harvest getHarvestById(UUID id);



    Crop addSubside (Crop crop, Subside subside);
    Crop removeSubside(Crop crop,Subside subside);


    CropSale addCropSale(MainCrop crop, CropSale cropSale);
    CropSale updateCropSale(CropSale cropSale);
    void removeCropSale(CropSale cropSale);
    CropSale getCropSaleById(UUID id);



}
