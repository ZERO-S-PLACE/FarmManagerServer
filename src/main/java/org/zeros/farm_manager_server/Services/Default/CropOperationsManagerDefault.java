package org.zeros.farm_manager_server.Services.Default;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.CustomException.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.CustomException.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.InterCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Subside;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.*;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Crop.CropSaleRepository;
import org.zeros.farm_manager_server.Services.Interface.CropOperationsManager;
import org.zeros.farm_manager_server.Services.Interface.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Data.*;
import org.zeros.farm_manager_server.Services.Interface.UserFieldsManager;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class CropOperationsManagerDefault implements CropOperationsManager {
    private final EntityManager entityManager;
    private final CropRepository cropRepository;
    private final CultivationRepository cultivationRepository;
    private final SeedingRepository seedingRepository;
    private final SprayApplicationRepository sprayApplicationRepository;
    private final FertilizerApplicationRepository fertilizerApplicationRepository;
    private final HarvestRepository harvestRepository;
    private final CropSaleRepository cropSaleRepository;
    private final SubsideManager subsideManager;
    private final FarmingMachineManager farmingMachineManager;
    private final CropParametersManager cropParametersManager;
    private final SprayManager sprayManager;
    private final FertilizerManager fertilizerManager;
    private final UserFieldsManager userFieldsManager;
    private final PlantManager plantManager;
    private final LoggedUserConfiguration loggedUserConfiguration;


    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public MainCrop createNewMainCrop(UUID fieldPartId, Set<UUID> cultivatedPlantsIds) {
        FieldPart fieldPart = getFieldPartIfExists(fieldPartId);
        Set<Plant> cultivatedPlants = getPlantsIfExist(cultivatedPlantsIds);
        Crop crop = MainCrop.builder().cultivatedPlants(cultivatedPlants).fieldPart(fieldPart).build();
        Crop cropSaved = cropRepository.saveAndFlush(crop);
        fieldPart.getCrops().add(cropSaved);
        flushChanges();
        return (MainCrop) getCropById(cropSaved.getId());
    }

    private FieldPart getFieldPartIfExists(UUID fieldPartId) {
        FieldPart fieldPart = userFieldsManager.getFieldPartById(fieldPartId);
        if (fieldPart == FieldPart.NONE) {
            throw new IllegalArgumentExceptionCustom(MainCrop.class, Set.of("fieldPart"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        return fieldPart;
    }

    private Set<Plant> getPlantsIfExist(Set<UUID> cultivatedPlantsIds) {
        if (cultivatedPlantsIds.isEmpty()) {
            throw new IllegalArgumentExceptionCustom(MainCrop.class, Set.of("cultivatedPlants"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        Set<Plant> cultivatedPlants = cultivatedPlantsIds.stream()
                .map(plantManager::getPlantById)
                .collect(Collectors.toSet());
        if (cultivatedPlants.contains(Plant.NONE) || cultivatedPlants.isEmpty()) {
            throw new IllegalArgumentExceptionCustom(MainCrop.class, Set.of("cultivatedPlants"),
                    IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return cultivatedPlants;
    }


    @Override
    public InterCrop createNewInterCrop(UUID fieldPartId, Set<UUID> cultivatedPlantsIds) {
        FieldPart fieldPart = getFieldPartIfExists(fieldPartId);
        Set<Plant> cultivatedPlants = getPlantsIfExist(cultivatedPlantsIds);
        Crop crop = InterCrop.builder().cultivatedPlants(cultivatedPlants).fieldPart(fieldPart).build();
        Crop cropSaved = cropRepository.saveAndFlush(crop);
        fieldPart.getCrops().add(cropSaved);
        flushChanges();
        return (InterCrop) getCropById(cropSaved.getId());
    }

    @Override
    public void deleteCropAndItsData(UUID cropId) {
        Crop crop = getCropById(cropId);
        if (crop == MainCrop.NONE) {
            return;
        }
        cropRepository.delete(crop);
        flushChanges();
    }

    @Override
    public Crop updateCultivatedPlants(UUID cropId, Set<UUID> cultivatedPlantsIds) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        Crop cropOriginal = cropRepository.findById(crop.getId()).orElse(MainCrop.NONE);
        if (cropOriginal.equals(MainCrop.NONE)) {
            return MainCrop.NONE;
        }
        Set<Plant> cultivatedPlants = getPlantsIfExist(cultivatedPlantsIds);
        cropOriginal.setCultivatedPlants(cultivatedPlants);
        cropRepository.save(cropOriginal);
        flushChanges();
        return getCropById(cropId);
    }

    private Crop getCropIfExists(UUID cropId) {
        Crop crop = getCropById(cropId);
        if (crop.equals(MainCrop.NONE) || crop.equals(InterCrop.NONE)) {
            throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return crop;
    }

    private void checkOperationModificationAccess(Crop crop) {
        if (crop.getWorkFinished()) {
            throw new IllegalAccessErrorCustom(Crop.class, IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
        }
    }

    @Override
    public Crop setDateDestroyed(UUID interCropId, LocalDate dateDestroyed) {
        Crop crop = getCropIfExists(interCropId);
        if (crop instanceof InterCrop) {
            ((InterCrop) crop).setDateDestroyed(dateDestroyed);
            flushChanges();
            return getCropById(crop.getId());
        }
        throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.TYPE_MISMATCH);
    }

    @Override
    public void setWorkFinished(UUID cropId) {
        Crop crop = getCropIfExists(cropId);
        for (AgriculturalOperation operation : crop.getAllOperations()) {
            if (operation.getIsPlannedOperation()) {
                deleteOperationByType(operation);
            }
        }
        crop.setWorkFinished(true);
        flushChanges();
        getCropById(crop.getId());
    }

    @Override
    public void deleteOperation(UUID operationId, OperationType operationType) {
        AgriculturalOperation operation = getOperationAccordingToType(operationId, operationType);
        deleteOperationByType(operation);
    }

    private void deleteOperationByType(AgriculturalOperation operation) {
        switch (operation.getOperationType()) {
            case CULTIVATION -> cultivationRepository.delete((Cultivation) operation);
            case SEEDING -> seedingRepository.delete((Seeding) operation);
            case FERTILIZER_APPLICATION -> fertilizerApplicationRepository.delete((FertilizerApplication) operation);
            case SPRAY_APPLICATION -> sprayApplicationRepository.delete((SprayApplication) operation);
            case HARVEST -> harvestRepository.delete((Harvest) operation);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        }
        flushChanges();
    }

    @Override
    public void setFullySold(UUID mainCropId) {
        Crop crop = getCropIfExists(mainCropId);
        if (crop instanceof MainCrop) {
            ((MainCrop) crop).setIsFullySold(true);
            flushChanges();
            return;
        }
        throw new IllegalArgumentExceptionCustom(Crop.class,
                IllegalArgumentExceptionCause.TYPE_MISMATCH);
    }

    @Override
    public Crop getCropById(UUID id) {
        return cropRepository.findById(id).orElse(MainCrop.NONE);
    }

    @Override
    public AgriculturalOperation commitPlannedOperation(UUID operationId, OperationType operationType) {
        AgriculturalOperation operation = getOperationAccordingToType(operationId, operationType);
        operation.setIsPlannedOperation(false);
        return saveOperationAccordingToType(operation);
    }

    @Override
    public AgriculturalOperation updateOperationMachine(UUID operationId, OperationType operationType, UUID farmingMachineId) {
        AgriculturalOperation operation = getOperationAccordingToType(operationId, operationType);
        checkOperationModificationAccess(operation.getCrop());
        FarmingMachine farmingMachine = getMachineIfExists(farmingMachineId);
        checkCompatibility(operationType, farmingMachine);
        operation.setFarmingMachine(farmingMachine);
        return saveOperationAccordingToType(operation);
    }

    private void checkCompatibility(OperationType operationType, FarmingMachine farmingMachine) {
        if (farmingMachine.getSupportedOperationTypes().contains(operationType) || farmingMachine.getSupportedOperationTypes().contains(OperationType.ANY)) {
            return;
        }
        throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                IllegalArgumentExceptionCause.NOT_COMPATIBLE);
    }

    private FarmingMachine getMachineIfExists(UUID farmingMachineId) {
        if (farmingMachineId == null) {
            return farmingMachineManager.getUndefinedFarmingMachine();
        }
        FarmingMachine farmingMachine = farmingMachineManager.getFarmingMachineById(farmingMachineId);
        if (farmingMachine == FarmingMachine.NONE) {
            throw new IllegalArgumentExceptionCustom(FarmingMachine.class,
                    IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return farmingMachine;
    }

    @Override
    public AgriculturalOperation updateOperationParameters(AgriculturalOperationDTO agriculturalOperationDTO) {
        AgriculturalOperation operation = getOperationAccordingToType(agriculturalOperationDTO.getId(), agriculturalOperationDTO.getOperationType());
        checkOperationModificationAccess(operation.getCrop());
        return saveOperationAccordingToType(rewriteOperationDTOToEntity(agriculturalOperationDTO, operation));
    }

    private AgriculturalOperation rewriteOperationDTOToEntity(AgriculturalOperationDTO dto, AgriculturalOperation entity) {
        switch (dto.getOperationType()) {
            case CULTIVATION -> {
                return rewriteToEntity((CultivationDTO) dto, (Cultivation) entity);
            }
            case SEEDING -> {
                return rewriteToEntity((SeedingDTO) dto, (Seeding) entity);
            }
            case FERTILIZER_APPLICATION -> {
                return rewriteToEntity((FertilizerApplicationDTO) dto, (FertilizerApplication) entity);
            }
            case SPRAY_APPLICATION -> {
                return rewriteToEntity((SprayApplicationDTO) dto, (SprayApplication) entity);
            }
            case HARVEST -> {
                return rewriteToEntity((HarvestDTO) dto, (Harvest) entity);
            }
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        }
    }


    public AgriculturalOperation getOperationAccordingToType(UUID operationId, OperationType operationType) {
        AgriculturalOperation operation;
        switch (operationType) {
            case CULTIVATION -> operation = getCultivationById(operationId);
            case SEEDING -> operation = getSeedingById(operationId);
            case FERTILIZER_APPLICATION -> operation = getFertilizerApplicationById(operationId);
            case SPRAY_APPLICATION -> operation = getSprayApplicationById(operationId);
            case HARVEST -> operation = getHarvestById(operationId);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        }
        if (operation.getId() == null) {
            throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return operation;
    }

    private AgriculturalOperation saveOperationAccordingToType(AgriculturalOperation agriculturalOperation) {
        switch (agriculturalOperation.getOperationType()) {
            case CULTIVATION -> {
                return cultivationRepository.saveAndFlush((Cultivation) agriculturalOperation);
            }
            case SEEDING -> {
                return seedingRepository.saveAndFlush((Seeding) agriculturalOperation);
            }
            case FERTILIZER_APPLICATION -> {
                return fertilizerApplicationRepository.saveAndFlush((FertilizerApplication) agriculturalOperation);
            }
            case SPRAY_APPLICATION -> {
                return sprayApplicationRepository.saveAndFlush((SprayApplication) agriculturalOperation);
            }
            case HARVEST -> {
                return harvestRepository.saveAndFlush((Harvest) agriculturalOperation);
            }
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        }
    }

    @Override
    public Seeding planSeeding(UUID cropId, SeedingDTO seedingDTO) {
        return createNewSeeding(cropId, seedingDTO, true);
    }

    @Override
    public Seeding addSeeding(UUID cropId, SeedingDTO seedingDTO) {
        return createNewSeeding(cropId, seedingDTO, false);
    }

    private Seeding createNewSeeding(UUID cropId, SeedingDTO seedingDTO, boolean planned) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        checkIfUniqueOperation(seedingDTO);
        Seeding seeding = rewriteToEntity(seedingDTO, Seeding.NONE);
        FarmingMachine farmingMachine = getMachineIfExists(seedingDTO.getFarmingMachine());
        checkCompatibility(OperationType.SEEDING, farmingMachine);
        seeding.setFarmingMachine(farmingMachine);
        seeding.setIsPlannedOperation(planned);
        seeding.setCrop(crop);
        Seeding seedingSaved = seedingRepository.saveAndFlush(seeding);
        crop.getSeeding().add(seedingSaved);
        flushChanges();
        return getSeedingById(seedingSaved.getId());

    }

    private Seeding rewriteToEntity(SeedingDTO dto, Seeding entity) {
        Seeding entityParsed = DefaultMappers.seedingMapper.dtoToEntitySimpleProperties(dto);
        entityParsed.setSownPlants(getPlantsIfExist(dto.getSownPlants()));
        rewriteNotModifiedParameters(entity, entityParsed);
        return entityParsed;
    }

    private void rewriteNotModifiedParameters(AgriculturalOperation entity, AgriculturalOperation entityParsed) {
        entityParsed.setCrop(entity.getCrop());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setFarmingMachine(entity.getFarmingMachine());
    }

    private void checkIfUniqueOperation(AgriculturalOperationDTO operationDTO) {
        if (operationDTO.getId() != null) {
            throw new IllegalArgumentExceptionCustom(Seeding.class, IllegalArgumentExceptionCause.OBJECT_EXISTS);
        }
    }

    @Override
    public Seeding getSeedingById(UUID id) {
        return seedingRepository.findById(id).orElse(Seeding.NONE);
    }

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

    @Override
    public void addSubside(UUID cropId, UUID subsideId) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        Subside subside = subsideManager.getSubsideById(subsideId);
        if (crop.getSubsides().contains(subside)) {
            return;
        }
        crop.getSubsides().add(subside);
        flushChanges();
    }

    @Override
    public void removeSubside(UUID cropId, UUID subsideId) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        Subside subside = subsideManager.getSubsideById(subsideId);
        crop.getSubsides().remove(subside);
        flushChanges();
    }

    @Override
    public CropSale addCropSale(UUID cropId, CropSaleDTO cropSaleDTO) {
        Crop crop = getCropIfExists(cropId);
        checkSaleModificationAccess(crop);
        Crop cropOriginal = cropRepository.findById(crop.getId()).orElse(MainCrop.NONE);
        checkIfUnique(cropSaleDTO);
        CropSale cropSale = rewriteToEntity(cropSaleDTO, CropSale.NONE);
        cropSale.setCrop(cropOriginal);
        CropSale cropSaleSaved = cropSaleRepository.saveAndFlush(cropSale);
        ((MainCrop) cropOriginal).getCropSales().add(cropSaleSaved);
        flushChanges();
        return getCropSaleById(cropSaleSaved.getId());
    }

    private void checkIfUnique(CropSaleDTO cropSaleDTO) {
        if (cropSaleDTO.getId() != null) {
            throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.OBJECT_EXISTS);
        }
    }

    private void checkSaleModificationAccess(Crop crop) {
        if (crop instanceof MainCrop) {
            if (((MainCrop) crop).getIsFullySold()) {
                throw new IllegalAccessErrorCustom(Crop.class, IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
            }
            return;
        }
        throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.TYPE_MISMATCH);
    }

    private CropSale rewriteToEntity(CropSaleDTO dto, CropSale entity) {
        CropSale entityParsed = DefaultMappers.cropSaleMapper.dtoToEntitySimpleProperties(dto);
        if (dto.getCropParameters() == null) {
            entityParsed.setCropParameters(cropParametersManager.getUndefinedCropParameters());
        } else {
            CropParameters cropParameters = cropParametersManager.getCropParametersById(dto.getCropParameters());
            if (cropParameters == CropParameters.NONE) {
                entityParsed.setCropParameters(cropParametersManager.getUndefinedCropParameters());
            } else {
                entityParsed.setCropParameters(cropParameters);
            }
        }
        entityParsed.setCrop(entity.getCrop());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    public CropSale updateCropSale(CropSaleDTO cropSaleDTO) {
        Crop crop = getCropIfExists(cropSaleDTO.getCrop());
        checkSaleModificationAccess(crop);
        CropSale cropSaleOriginal = getCropSaleIfExist(cropSaleDTO);
        return cropSaleRepository.saveAndFlush(rewriteToEntity(cropSaleDTO, cropSaleOriginal));
    }

    private CropSale getCropSaleIfExist(CropSaleDTO cropSaleDTO) {
        if (cropSaleDTO.getId() == null) {
            throw new IllegalArgumentExceptionCustom(CropSale.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        CropSale cropSaleOriginal = getCropSaleById(cropSaleDTO.getId());
        if (cropSaleOriginal.equals(CropSale.NONE)) {
            throw new IllegalArgumentExceptionCustom(CropSale.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return cropSaleOriginal;
    }

    @Override
    public void removeCropSale(UUID cropSaleId) {
        CropSale cropSale = getCropSaleById(cropSaleId);
        if (cropSale == CropSale.NONE) {
            return;
        }
        checkSaleModificationAccess(cropSale.getCrop());
        MainCrop crop = (MainCrop) cropSale.getCrop();
        crop.getCropSales().remove(cropSale);
        cropSaleRepository.delete(cropSale);
        flushChanges();
    }

    @Override
    public CropSale getCropSaleById(UUID id) {
        return cropSaleRepository.findById(id).orElse(CropSale.NONE);
    }

}
