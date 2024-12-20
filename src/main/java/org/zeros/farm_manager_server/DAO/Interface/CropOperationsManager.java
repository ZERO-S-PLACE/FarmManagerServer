package org.zeros.farm_manager_server.DAO.Interface;

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

public interface CropOperationsManager {

    Crop createNewMainCrop(FieldPart fieldPart, Set<Plant> cultivatedPlants);
    Crop createNewInterCrop(FieldPart fieldPart, Set<Plant> cultivatedPlants);
    void deleteCropAndItsData(Crop crop);
    Crop updateCultivatedPlants(Crop crop,Set<Plant> cultivatedPlants);


    Seeding planSeeding(Crop crop,Seeding seeding);
    Seeding addSeeding(Crop crop,Seeding seeding);
    void deleteSeeding(Crop crop,Seeding seeding);

    Cultivation planCultivation(Crop crop, Cultivation cultivation);
    Cultivation addCultivation(Crop crop,Cultivation cultivation);
    void deleteCultivation(Crop crop,Cultivation cultivation);

    FertilizerApplication planFertilizerApplication(Crop crop, FertilizerApplication fertilizerApplication);
    FertilizerApplication addFertilizerApplication(Crop crop,FertilizerApplication fertilizerApplication);
    void deleteFertilizerApplication(Crop crop,FertilizerApplication fertilizerApplication);

    SprayApplication planSprayApplication(Crop crop, SprayApplication sprayApplication);
    SprayApplication addSprayApplication(Crop crop,SprayApplication sprayApplication);
    void deleteSprayApplication(Crop crop,SprayApplication sprayApplication);

    Cultivation planHarvest(MainCrop crop, Harvest harvest);
    Cultivation addHarvest(MainCrop crop,Harvest harvest,Boolean workFinished);
    void deleteHarvest(MainCrop crop,Harvest harvest);

    InterCrop setDateDestroyed(InterCrop interCrop, LocalDate dateDestroyed);

    Subside addSubside (Crop crop, Subside subside);
    void removeSubside(Crop crop,Subside subside);

    CropSale addCropSale(Crop crop, CropSale cropSale, Boolean isFullySold);
    void removeCropSale(Crop crop, CropSale cropSale, Boolean isFullySold);


}
