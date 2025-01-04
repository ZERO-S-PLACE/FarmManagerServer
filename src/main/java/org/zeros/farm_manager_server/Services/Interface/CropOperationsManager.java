package org.zeros.farm_manager_server.Services.Interface;

import jakarta.validation.constraints.NotNull;
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

    MainCrop createNewMainCrop(@NotNull UUID fieldPartId, @NotNull Set<UUID> cultivatedPlantsIds);

    InterCrop createNewInterCrop(@NotNull UUID fieldPartId,@NotNull Set<UUID> cultivatedPlantsIds);

    void deleteCropAndItsData(@NotNull UUID cropId);

    Crop updateCultivatedPlants(@NotNull UUID cropId, @NotNull Set<UUID> cultivatedPlantsIds);

    Crop setDateDestroyed(@NotNull UUID interCropId, @NotNull LocalDate dateDestroyed);

    void setWorkFinished(@NotNull UUID cropId);

    void setFullySold(@NotNull UUID mainCropId);

    Crop getCropById(@NotNull UUID cropId);

    AgriculturalOperation commitPlannedOperation(@NotNull UUID operationId, @NotNull OperationType operationType);

    AgriculturalOperation updateOperationMachine(@NotNull UUID operationId, @NotNull OperationType operationType,@NotNull UUID machineId);

    AgriculturalOperation updateOperationParameters(@NotNull AgriculturalOperationDTO agriculturalOperationDTO);

    void deleteOperation(@NotNull UUID operationId,@NotNull OperationType operationType);

    Seeding planSeeding(@NotNull UUID cropId, @NotNull SeedingDTO seedingDTO);

    Seeding addSeeding(@NotNull UUID cropId, @NotNull SeedingDTO seedingDTO);

    Seeding getSeedingById(@NotNull UUID id);

    Cultivation planCultivation(@NotNull UUID cropId, @NotNull CultivationDTO cultivationDTO);

    Cultivation addCultivation(@NotNull UUID cropId, @NotNull CultivationDTO cultivationDTO);

    Cultivation getCultivationById(@NotNull UUID id);


    FertilizerApplication planFertilizerApplication(@NotNull UUID cropId, @NotNull FertilizerApplicationDTO fertilizerApplicationDTO);

    FertilizerApplication addFertilizerApplication(@NotNull UUID cropId, @NotNull FertilizerApplicationDTO fertilizerApplicationDTO);

    FertilizerApplication getFertilizerApplicationById(@NotNull UUID id);


    SprayApplication planSprayApplication(@NotNull UUID cropId, @NotNull SprayApplicationDTO sprayApplicationDTO);

    SprayApplication addSprayApplication(@NotNull UUID cropId, @NotNull SprayApplicationDTO sprayApplicationDTO);

    SprayApplication getSprayApplicationById(@NotNull UUID id);


    Harvest planHarvest(@NotNull UUID cropId,@NotNull HarvestDTO harvestDTO);

    Harvest addHarvest(@NotNull UUID cropId, @NotNull HarvestDTO harvestDTO);

    Harvest getHarvestById(@NotNull UUID id);


    void addSubside(@NotNull UUID cropId, @NotNull UUID subsideId);

    void removeSubside(@NotNull UUID cropId,@NotNull UUID subsideId);

    CropSale addCropSale(@NotNull UUID cropId, @NotNull CropSaleDTO cropSaleDTO);

    CropSale updateCropSale(@NotNull CropSaleDTO cropSaleDTO);

    void removeCropSale(@NotNull UUID cropSaleId);

    CropSale getCropSaleById(@NotNull UUID id);


}
