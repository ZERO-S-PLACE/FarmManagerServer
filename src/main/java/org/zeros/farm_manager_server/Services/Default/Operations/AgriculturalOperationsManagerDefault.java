package org.zeros.farm_manager_server.Services.Default.Operations;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Domain.DTO.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.Operations.*;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.AgriculturalOperationsManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.OperationManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class AgriculturalOperationsManagerDefault implements AgriculturalOperationsManager {
    private final EntityManager entityManager;
    private final FarmingMachineManager farmingMachineManager;
    private final OperationManager<Seeding, SeedingDTO> seedingManager;
    private final OperationManager<Cultivation, CultivationDTO> cultivationManager;
    private final OperationManager<FertilizerApplication, FertilizerApplicationDTO> fertilizerApplicationManager;
    private final OperationManager<SprayApplication, SprayApplicationDTO> sprayApplicationManager;
    private final OperationManager<Harvest, HarvestDTO> harvestManager;

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }



    @Override
    public AgriculturalOperation getOperationById(UUID operationId, OperationType operationType) {
        return switch (operationType) {
            case CULTIVATION -> cultivationManager.getOperationById(operationId);
            case SEEDING -> seedingManager.getOperationById(operationId);
            case FERTILIZER_APPLICATION -> fertilizerApplicationManager.getOperationById(operationId);
            case SPRAY_APPLICATION -> sprayApplicationManager.getOperationById(operationId);
            case HARVEST -> harvestManager.getOperationById(operationId);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
    }

    @Override
    public AgriculturalOperation planOperation(UUID cropId,AgriculturalOperationDTO operationDTO) {
            return switch (operationDTO) {
                case SeedingDTO seedingDTO -> seedingManager.planOperation(cropId,seedingDTO);
                case CultivationDTO cultivationDTO -> cultivationManager.planOperation(cropId,cultivationDTO);
                case FertilizerApplicationDTO fertilizerApplicationDTO -> fertilizerApplicationManager.planOperation(cropId,fertilizerApplicationDTO);
                case SprayApplicationDTO sprayApplicationDTO -> sprayApplicationManager.planOperation(cropId,sprayApplicationDTO);
                case HarvestDTO harvestDTO -> harvestManager.planOperation(cropId,harvestDTO);
                default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                        IllegalArgumentExceptionCause.TYPE_MISMATCH);
            };
    }

    @Override
    public AgriculturalOperation addOperation(UUID cropId,AgriculturalOperationDTO operationDTO) {
        return switch (operationDTO) {
            case SeedingDTO seedingDTO -> seedingManager.addOperation(cropId,seedingDTO);
            case CultivationDTO cultivationDTO -> cultivationManager.addOperation(cropId,cultivationDTO);
            case FertilizerApplicationDTO fertilizerApplicationDTO -> fertilizerApplicationManager.addOperation(cropId,fertilizerApplicationDTO);
            case SprayApplicationDTO sprayApplicationDTO -> sprayApplicationManager.addOperation(cropId,sprayApplicationDTO);
            case HarvestDTO harvestDTO -> harvestManager.addOperation(cropId,harvestDTO);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
    }



    @Override
    public void setPlannedOperationPerformed(UUID operationId, OperationType operationType) {
        AgriculturalOperation operation = getOperationById(operationId, operationType);
        operation.setIsPlannedOperation(false);
        flushChanges();
    }

    @Override
    public void updateOperationMachine(UUID operationId, OperationType operationType, UUID farmingMachineId) {
        AgriculturalOperation operation = getOperationById(operationId, operationType);
        FarmingMachine farmingMachine = farmingMachineManager.getFarmingMachineIfCompatible(
                farmingMachineId,operationType);
        operation.setFarmingMachine(farmingMachine);
        flushChanges();
    }


    @Override
    public void updateOperationParameters(AgriculturalOperationDTO operationDTO) {
        switch (operationDTO) {
            case SeedingDTO seedingDTO -> seedingManager.updateOperation(seedingDTO);
            case CultivationDTO cultivationDTO -> cultivationManager.updateOperation(cultivationDTO);
            case FertilizerApplicationDTO fertilizerApplicationDTO -> fertilizerApplicationManager.updateOperation(fertilizerApplicationDTO);
            case SprayApplicationDTO sprayApplicationDTO -> sprayApplicationManager.updateOperation(sprayApplicationDTO);
            case HarvestDTO harvestDTO -> harvestManager.updateOperation(harvestDTO);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        }
        flushChanges();
    }

    @Override
    public void deleteOperation(UUID operationId, OperationType operationType) {
        switch (operationType) {
            case CULTIVATION -> cultivationManager.deleteOperation(operationId);
            case SEEDING -> seedingManager.deleteOperation(operationId);
            case FERTILIZER_APPLICATION -> fertilizerApplicationManager.deleteOperation(operationId);
            case SPRAY_APPLICATION -> sprayApplicationManager.deleteOperation(operationId);
            case HARVEST -> harvestManager.deleteOperation(operationId);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        }
        flushChanges();
    }

  /*

    @Override
    public Cultivation planCultivation(UUID cropId, CultivationDTO cultivationDTO) {
        return createNewCultivation(cropId, cultivationDTO, true);
    }

    @Override
    public Cultivation addCultivation(UUID cropId, CultivationDTO cultivationDTO) {
        return createNewCultivation(cropId, cultivationDTO, false);
    }

    private Cultivation createNewCultivation(UUID cropId, CultivationDTO cultivationDTO, boolean planned) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        checkIfUniqueOperation(cultivationDTO);
        Cultivation cultivation = rewriteToEntity(cultivationDTO, Cultivation.NONE);
        FarmingMachine farmingMachine = getMachineIfExists(cultivationDTO.getFarmingMachine());
        checkCompatibility(OperationType.CULTIVATION, farmingMachine);
        cultivation.setFarmingMachine(farmingMachine);
        cultivation.setIsPlannedOperation(planned);
        cultivation.setCrop(crop);
        Cultivation cultivationSaved = cultivationRepository.saveAndFlush(cultivation);
        crop.getCultivations().add(cultivationSaved);
        flushChanges();
        return getCultivationById(cultivationSaved.getId());

    }

    private Cultivation rewriteToEntity(CultivationDTO dto, Cultivation entity) {
        Cultivation entityParsed = DefaultMappers.cultivationMapper.dtoToEntitySimpleProperties(dto);
        rewriteNotModifiedParameters(entity, entityParsed);
        return entityParsed;
    }

    @Override
    public Cultivation getCultivationById(UUID id) {
        return cultivationRepository.findById(id).orElse(Cultivation.NONE);
    }

    @Override
    public FertilizerApplication planFertilizerApplication(UUID cropId, FertilizerApplicationDTO fertilizerApplicationDTO) {
        return createNewFertilizerApplication(cropId, fertilizerApplicationDTO, true);
    }

    @Override
    public FertilizerApplication addFertilizerApplication(UUID cropId, FertilizerApplicationDTO fertilizerApplicationDTO) {
        return createNewFertilizerApplication(cropId, fertilizerApplicationDTO, false);
    }

    private FertilizerApplication createNewFertilizerApplication(UUID cropId, FertilizerApplicationDTO fertilizerApplicationDTO, boolean planned) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        checkIfUniqueOperation(fertilizerApplicationDTO);
        FertilizerApplication fertilizerApplication = rewriteToEntity(fertilizerApplicationDTO, FertilizerApplication.NONE);
        FarmingMachine farmingMachine = getMachineIfExists(fertilizerApplicationDTO.getFarmingMachine());
        checkCompatibility(OperationType.FERTILIZER_APPLICATION, farmingMachine);
        fertilizerApplication.setFarmingMachine(farmingMachine);
        fertilizerApplication.setIsPlannedOperation(planned);
        fertilizerApplication.setCrop(crop);
        FertilizerApplication fertilizerApplicationSaved = fertilizerApplicationRepository.saveAndFlush(fertilizerApplication);
        crop.getFertilizerApplications().add(fertilizerApplicationSaved);
        flushChanges();
        return getFertilizerApplicationById(fertilizerApplicationSaved.getId());
    }

    private FertilizerApplication rewriteToEntity(FertilizerApplicationDTO dto, FertilizerApplication entity) {
        FertilizerApplication entityParsed = DefaultMappers.fertilizerApplicationMapper.dtoToEntitySimpleProperties(dto);
        Fertilizer fertilizer = getFertilizerIfExist(dto.getFertilizer());
        if (fertilizer.equals(fertilizerManager.getUndefinedFertilizer())) {
            throw new IllegalArgumentExceptionCustom(FertilizerApplication.class, Set.of("fertilizer"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        entityParsed.setFertilizer(fertilizer);
        rewriteNotModifiedParameters(entity, entityParsed);
        return entityParsed;
    }


    @Override
    public FertilizerApplication getFertilizerApplicationById(UUID id) {
        return fertilizerApplicationRepository.findById(id).orElse(FertilizerApplication.NONE);
    }

    @Override
    public SprayApplication planSprayApplication(UUID cropId, SprayApplicationDTO sprayApplicationDTO) {
        return createNewSprayApplication(cropId, sprayApplicationDTO, true);
    }

    @Override
    public SprayApplication addSprayApplication(UUID cropId, SprayApplicationDTO sprayApplicationDTO) {
        return createNewSprayApplication(cropId, sprayApplicationDTO, false);
    }

    private SprayApplication createNewSprayApplication(UUID cropId, SprayApplicationDTO sprayApplicationDTO, boolean planned) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        checkIfUniqueOperation(sprayApplicationDTO);
        SprayApplication sprayApplication = rewriteToEntity(sprayApplicationDTO, SprayApplication.NONE);
        FarmingMachine farmingMachine = getMachineIfExists(sprayApplicationDTO.getFarmingMachine());
        checkCompatibility(OperationType.SPRAY_APPLICATION, farmingMachine);
        sprayApplication.setFarmingMachine(farmingMachine);
        sprayApplication.setIsPlannedOperation(planned);
        sprayApplication.setCrop(crop);
        SprayApplication sprayApplicationSaved = sprayApplicationRepository.saveAndFlush(sprayApplication);
        crop.getSprayApplications().add(sprayApplicationSaved);
        flushChanges();
        return getSprayApplicationById(sprayApplicationSaved.getId());
    }

    private SprayApplication rewriteToEntity(SprayApplicationDTO dto, SprayApplication entity) {
        SprayApplication entityParsed = DefaultMappers.sprayApplicationMapper.dtoToEntitySimpleProperties(dto);
        Fertilizer fertilizer = getFertilizerIfExist(dto.getFertilizer());
        Spray spray = getSprayIfExists(dto.getSpray());
        if (fertilizer.equals(fertilizerManager.getUndefinedFertilizer()) && spray.equals(sprayManager.getUndefinedSpray())) {
            throw new IllegalArgumentExceptionCustom(FertilizerApplication.class, Set.of("fertilizer", "spray"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        entityParsed.setFertilizer(fertilizer);
        entityParsed.setSpray(spray);
        rewriteNotModifiedParameters(entity, entityParsed);
        return entityParsed;
    }

    private Fertilizer getFertilizerIfExist(UUID fertilizerId) {
        if (fertilizerId == null) {
            return fertilizerManager.getUndefinedFertilizer();
        }
        Fertilizer fertilizer = fertilizerManager.getFertilizerById(fertilizerId);
        if (fertilizer.equals(Fertilizer.NONE)) {
            throw new IllegalArgumentExceptionCustom(Fertilizer.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return fertilizer;
    }

    private Spray getSprayIfExists(UUID sprayId) {
        if (sprayId == null) {
            return sprayManager.getUndefinedSpray();
        }
        Spray spray = sprayManager.getSprayById(sprayId);
        if (spray.equals(Spray.NONE)) {
            throw new IllegalArgumentExceptionCustom(Fertilizer.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return spray;
    }


    @Override
    public SprayApplication getSprayApplicationById(UUID id) {
        return sprayApplicationRepository.findById(id).orElse(SprayApplication.NONE);
    }

    @Override
    public Harvest planHarvest(UUID cropId, HarvestDTO harvestDTO) {
        return createNewHarvest(cropId, harvestDTO, true);
    }

    @Override
    public Harvest addHarvest(UUID cropId, HarvestDTO harvestDTO) {
        return createNewHarvest(cropId, harvestDTO, false);
    }

    private Harvest createNewHarvest(UUID cropId, HarvestDTO harvestDTO, boolean planned) {
        Crop crop = getCropIfExists(cropId);
        if (!(crop instanceof MainCrop)) {
            throw new IllegalArgumentExceptionCustom(Harvest.class, IllegalArgumentExceptionCause.TYPE_MISMATCH);
        }
        checkOperationModificationAccess(crop);
        checkIfUniqueOperation(harvestDTO);
        Harvest harvest = rewriteToEntity(harvestDTO, Harvest.NONE);
        FarmingMachine farmingMachine = getMachineIfExists(harvestDTO.getFarmingMachine());
        checkCompatibility(OperationType.HARVEST, farmingMachine);
        harvest.setFarmingMachine(farmingMachine);
        harvest.setIsPlannedOperation(planned);
        harvest.setCrop(crop);
        Harvest harvestSaved = harvestRepository.saveAndFlush(harvest);
        ((MainCrop) crop).getHarvest().add(harvestSaved);
        flushChanges();
        return getHarvestById(harvestSaved.getId());
    }

    private Harvest rewriteToEntity(HarvestDTO dto, Harvest entity) {
        Harvest entityParsed = DefaultMappers.harvestMapper.dtoToEntitySimpleProperties(dto);
        CropParameters cropParameters = cropParametersManager.getCropParametersById(dto.getCropParameters());
        if (cropParameters == CropParameters.NONE) {
            cropParameters = cropParametersManager.getUndefinedCropParameters();
        }
        entityParsed.setCropParameters(cropParameters);
        rewriteNotModifiedParameters(entity, entityParsed);
        return entityParsed;
    }

    @Override
    public Harvest getHarvestById(UUID id) {
        return harvestRepository.findById(id).orElse(Harvest.NONE);
    }
*/

}
