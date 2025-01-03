package org.zeros.farm_manager_server.Services.Interface;

import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.InterCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface CropOperationsManager {

    MainCrop createNewMainCrop(UUID fieldPartId, Set<UUID> cultivatedPlantsIds);

    InterCrop createNewInterCrop(UUID fieldPartId, Set<UUID> cultivatedPlantsIds);

    void deleteCropAndItsData(UUID cropId);

    Crop updateCultivatedPlants(UUID cropId, Set<UUID> cultivatedPlantsIds);

    Crop setDateDestroyed(UUID interCropId, LocalDate dateDestroyed);

    void setWorkFinished(UUID cropId);

    void setFullySold(UUID mainCropId);

    Crop getCropById(UUID id);

    AgriculturalOperation commitPlannedOperation(UUID operationId, OperationType operationType);

    AgriculturalOperation updateOperationMachine(UUID operationId, OperationType operationType, UUID machineId);

    AgriculturalOperation updateOperationParameters(AgriculturalOperationDTO agriculturalOperationDTO);

    void deleteOperation(UUID operationId, OperationType operationType);

    Seeding planSeeding(UUID cropId, SeedingDTO seedingDTO);

    Seeding addSeeding(UUID cropId, SeedingDTO seedingDTO);

    Seeding getSeedingById(UUID id);

    Cultivation planCultivation(UUID cropId, CultivationDTO cultivationDTO);

    Cultivation addCultivation(UUID cropId, CultivationDTO cultivationDTO);

    Cultivation getCultivationById(UUID id);


    FertilizerApplication planFertilizerApplication(UUID cropId, FertilizerApplicationDTO fertilizerApplicationDTO);

    FertilizerApplication addFertilizerApplication(UUID cropId, FertilizerApplicationDTO fertilizerApplicationDTO);

    FertilizerApplication getFertilizerApplicationById(UUID id);


    SprayApplication planSprayApplication(UUID cropId, SprayApplicationDTO sprayApplicationDTO);

    SprayApplication addSprayApplication(UUID cropId, SprayApplicationDTO sprayApplicationDTO);

    SprayApplication getSprayApplicationById(UUID id);


    Harvest planHarvest(UUID cropId, HarvestDTO harvestDTO);

    Harvest addHarvest(UUID cropId, HarvestDTO harvestDTO);

    Harvest getHarvestById(UUID id);


    void addSubside(UUID cropId, UUID subsideId);

    void removeSubside(UUID cropId, UUID subsideId);

    CropSale addCropSale(UUID cropId, CropSaleDTO cropSaleDTO);

    CropSale updateCropSale(CropSaleDTO cropSaleDTO);

    void removeCropSale(UUID cropSaleId);

    CropSale getCropSaleById(UUID id);


}
