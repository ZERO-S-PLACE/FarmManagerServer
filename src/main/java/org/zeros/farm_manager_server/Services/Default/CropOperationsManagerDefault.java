package org.zeros.farm_manager_server.Services.Default;

import jakarta.persistence.EntityManager;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Services.Interface.CropOperationsManager;
import org.zeros.farm_manager_server.Services.Interface.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;
import org.zeros.farm_manager_server.Services.Interface.Data.SprayManager;
import org.zeros.farm_manager_server.Services.Interface.Data.SubsideManager;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Entities.Crop.Crop.InterCrop;
import org.zeros.farm_manager_server.Entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Entities.Crop.CropSale;
import org.zeros.farm_manager_server.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Entities.Crop.Subside;
import org.zeros.farm_manager_server.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.*;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Crop.CropSaleRepository;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
@Primary
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

    public CropOperationsManagerDefault(CropRepository cropRepository,
                                        EntityManager entityManager,
                                        CultivationRepository cultivationRepository,
                                        SeedingRepository seedingRepository,
                                        SprayApplicationRepository sprayApplicationRepository,
                                        FertilizerApplicationRepository fertilizerApplicationRepository,
                                        HarvestRepository harvestRepository,
                                        CropSaleRepository cropSaleRepository,
                                        SubsideManager subsideManager,
                                        FarmingMachineManager farmingMachineManager,
                                        CropParametersManager cropParametersManager,
                                        SprayManager sprayManager,
                                        FertilizerManager fertilizerManager) {
        this.cropRepository = cropRepository;
        this.entityManager = entityManager;
        this.cultivationRepository = cultivationRepository;
        this.seedingRepository = seedingRepository;
        this.sprayApplicationRepository = sprayApplicationRepository;
        this.fertilizerApplicationRepository = fertilizerApplicationRepository;
        this.harvestRepository = harvestRepository;
        this.cropSaleRepository = cropSaleRepository;
        this.subsideManager = subsideManager;
        this.farmingMachineManager = farmingMachineManager;
        this.cropParametersManager = cropParametersManager;
        this.sprayManager = sprayManager;
        this.fertilizerManager = fertilizerManager;
    }

    private static void checkModificationAccess(Crop crop) {
        if (crop.getWorkFinished()) {
            throw new IllegalAccessError("Crop marked as finished, cannot modify");
        }
    }

    private static void checkSaleAccess(MainCrop crop) {
        if (crop.getIsFullySold()) {
            throw new IllegalArgumentException("Crop marked as fully sold,cannot modify");
        }
    }

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public MainCrop createNewMainCrop(@NonNull FieldPart fieldPart, @NonNull Set<Plant> cultivatedPlants) {
        Crop crop = MainCrop.builder().cultivatedPlants(cultivatedPlants).fieldPart(fieldPart).build();
        Crop cropSaved = cropRepository.saveAndFlush(crop);
        fieldPart.getCrops().add(cropSaved);
        flushChanges();
        return (MainCrop) getCropById(cropSaved.getId());
    }

    @Override
    public InterCrop createNewInterCrop(@NonNull FieldPart fieldPart, @NonNull Set<Plant> cultivatedPlants) {
        Crop crop = InterCrop.builder().fieldPart(fieldPart).build();
        Crop cropSaved = cropRepository.saveAndFlush(crop);
        fieldPart.getCrops().add(cropSaved);
        flushChanges();
        return (InterCrop) getCropById(cropSaved.getId());
    }

    @Override
    public void deleteCropAndItsData(@NonNull Crop crop) {
        cropRepository.delete(crop);
        flushChanges();
    }

    @Override
    public Crop updateCultivatedPlants(@NonNull Crop crop, @NonNull Set<Plant> cultivatedPlants) {

        checkModificationAccess(crop);

        Crop cropOriginal = cropRepository.findById(crop.getId()).orElse(MainCrop.NONE);
        if (cropOriginal.equals(MainCrop.NONE)) {
            return MainCrop.NONE;
        }
        cropOriginal.setCultivatedPlants(cultivatedPlants);
        cropRepository.save(cropOriginal);
        flushChanges();
        return cropRepository.findById(crop.getId()).orElse(MainCrop.NONE);
    }

    @Override
    public Crop setDateDestroyed(@NonNull InterCrop interCrop, @NonNull LocalDate dateDestroyed) {
        interCrop = (InterCrop) cropRepository.findById(interCrop.getId()).orElse(InterCrop.NONE);
        interCrop.setDateDestroyed(dateDestroyed);
        flushChanges();
        return getCropById(interCrop.getId());
    }

    @Override
    public void setWorkFinished(@NonNull Crop crop) {
        crop = getCropById(crop.getId());
        for (AgriculturalOperation operation : crop.getAllOperations()) {
            if (operation.getIsPlannedOperation()) {
                deleteOperationByType(operation);
            }
        }
        crop.setWorkFinished(true);
        flushChanges();
        getCropById(crop.getId());
    }

    private void deleteOperationByType(AgriculturalOperation operation) {
        if (operation instanceof Seeding) deleteSeeding((Seeding) operation);
        if (operation instanceof Cultivation) deleteCultivation((Cultivation) operation);
        if (operation instanceof FertilizerApplication) deleteFertilizerApplication((FertilizerApplication) operation);
        if (operation instanceof SprayApplication) deleteSprayApplication((SprayApplication) operation);
        if (operation instanceof Harvest) deleteHarvest((Harvest) operation);
    }

    @Override
    public void setFullySold(@NonNull MainCrop mainCrop) {
        mainCrop = (MainCrop) getCropById(mainCrop.getId());
        mainCrop.setIsFullySold(true);
        flushChanges();
        getCropById(mainCrop.getId());
    }

    @Override
    public Crop getCropById(@NonNull UUID id) {
        return cropRepository.findById(id).orElse(MainCrop.NONE);
    }

    @Override
    public AgriculturalOperation commitPlannedOperation(AgriculturalOperation agriculturalOperation) {
        checkModificationAccess(agriculturalOperation.getCrop());
        agriculturalOperation.setIsPlannedOperation(false);
        return saveOperationAccordingToType(agriculturalOperation);
    }

    @Override
    public AgriculturalOperation updateOperationMachine(AgriculturalOperation agriculturalOperation, FarmingMachine farmingMachine) {
        checkModificationAccess(agriculturalOperation.getCrop());
        if (farmingMachine.equals(FarmingMachine.UNDEFINED)) {
            agriculturalOperation.setFarmingMachine(farmingMachineManager.getUndefinedFarmingMachine());
            return saveOperationAccordingToType(agriculturalOperation);
        }
        if (farmingMachine.getSupportedOperationTypes().contains(agriculturalOperation.getOperationType())) {
            agriculturalOperation.setFarmingMachine(farmingMachine);
            return saveOperationAccordingToType(agriculturalOperation);
        }
        throw new IllegalArgumentException("This operation is not possible using selected machine");
    }

    @Override
    public AgriculturalOperation updateOperationParameters(AgriculturalOperation agriculturalOperation) {
        checkModificationAccess(agriculturalOperation.getCrop());
        AgriculturalOperation operationOriginal = getOperationAccordingToType(agriculturalOperation);
        agriculturalOperation.setCrop(operationOriginal.getCrop());
        agriculturalOperation.setFarmingMachine(operationOriginal.getFarmingMachine());
        agriculturalOperation.setIsPlannedOperation(operationOriginal.getIsPlannedOperation());
        return saveOperationAccordingToType(agriculturalOperation);
    }

    public AgriculturalOperation getOperationAccordingToType(AgriculturalOperation agriculturalOperation) {
        if (agriculturalOperation instanceof Seeding) {
            return getSeedingById(agriculturalOperation.getId());
        } else if (agriculturalOperation instanceof Cultivation) {
            return getCultivationById(agriculturalOperation.getId());
        } else if (agriculturalOperation instanceof FertilizerApplication) {
            return getFertilizerApplicationById(agriculturalOperation.getId());
        } else if (agriculturalOperation instanceof SprayApplication) {
            return getSprayApplicationById(agriculturalOperation.getId());
        } else if (agriculturalOperation instanceof Harvest) {
            return getHarvestById(agriculturalOperation.getId());
        }
        throw new IllegalArgumentException("Not supported operation type");
    }

    private AgriculturalOperation saveOperationAccordingToType(AgriculturalOperation agriculturalOperation) {
        if (agriculturalOperation instanceof Seeding) {
            return seedingRepository.saveAndFlush((Seeding) agriculturalOperation);
        } else if (agriculturalOperation instanceof Cultivation) {
            return cultivationRepository.saveAndFlush((Cultivation) agriculturalOperation);
        } else if (agriculturalOperation instanceof FertilizerApplication) {
            return fertilizerApplicationRepository.saveAndFlush((FertilizerApplication) agriculturalOperation);
        } else if (agriculturalOperation instanceof SprayApplication) {
            return sprayApplicationRepository.saveAndFlush((SprayApplication) agriculturalOperation);
        } else if (agriculturalOperation instanceof Harvest) {
            return harvestRepository.saveAndFlush((Harvest) agriculturalOperation);
        }
        throw new IllegalArgumentException("Not supported operation type");
    }

    @Override
    public Seeding planSeeding(@NonNull Crop crop, @NonNull Seeding seeding) {
        return createNewSeeding(crop, seeding, true);
    }

    @Override
    public Seeding addSeeding(@NonNull Crop crop, @NonNull Seeding seeding) {
        return createNewSeeding(crop, seeding, false);
    }

    public Seeding createNewSeeding(Crop crop, Seeding seeding, boolean planned) {
        checkModificationAccess(crop);
        Crop cropOriginal = cropRepository.findById(crop.getId()).orElse(MainCrop.NONE);
        if (cropOriginal.equals(MainCrop.NONE) || seeding.getId() != null) {
            return Seeding.NONE;
        }
        if (seeding.getFarmingMachine().equals(FarmingMachine.UNDEFINED)) {
            seeding.setFarmingMachine(farmingMachineManager.getUndefinedFarmingMachine());
        }

        if (seeding.getFarmingMachine().getSupportedOperationTypes().contains(OperationType.SEEDING) ||
                seeding.getFarmingMachine().getSupportedOperationTypes().contains(OperationType.ANY)) {
            seeding.setIsPlannedOperation(planned);
            seeding.setCrop(cropOriginal);
            Seeding seedingSaved = seedingRepository.saveAndFlush(seeding);
            cropOriginal.getSeeding().add(seedingSaved);
            flushChanges();
            return getSeedingById(seedingSaved.getId());
        }
        throw new IllegalArgumentException("Seeding is not possible using selected machine");
    }

    @Override
    public void deleteSeeding(@NonNull Seeding seeding) {
        checkModificationAccess(seeding.getCrop());
        seeding.getCrop().getSeeding().remove(seeding);
        seedingRepository.delete(seeding);
        flushChanges();
    }

    @Override
    public Seeding getSeedingById(UUID id) {
        return seedingRepository.findById(id).orElse(Seeding.NONE);
    }

    @Override
    public Cultivation planCultivation(Crop crop, Cultivation cultivation) {
        return createNewCultivation(crop, cultivation, true);
    }

    @Override
    public Cultivation addCultivation(Crop crop, Cultivation cultivation) {
        return createNewCultivation(crop, cultivation, false);
    }

    private Cultivation createNewCultivation(Crop crop, Cultivation cultivation, boolean planned) {
        checkModificationAccess(crop);
        Crop cropOriginal = cropRepository.findById(crop.getId()).orElse(MainCrop.NONE);
        if (cropOriginal.equals(MainCrop.NONE) || cultivation.getId() != null) {
            return Cultivation.NONE;
        }
        if (cultivation.getFarmingMachine().equals(FarmingMachine.UNDEFINED)) {
            cultivation.setFarmingMachine(farmingMachineManager.getUndefinedFarmingMachine());
        }

        if (cultivation.getFarmingMachine().getSupportedOperationTypes().contains(OperationType.CULTIVATION) ||
                cultivation.getFarmingMachine().getSupportedOperationTypes().contains(OperationType.ANY)) {
            cultivation.setIsPlannedOperation(planned);
            cultivation.setCrop(cropOriginal);
            Cultivation cultivationSaved = cultivationRepository.saveAndFlush(cultivation);
            cropOriginal.getCultivations().add(cultivationSaved);
            flushChanges();
            return getCultivationById(cultivationSaved.getId());
        }
        throw new IllegalArgumentException("Cultivation is not possible using selected machine");

    }

    @Override
    public void deleteCultivation(Cultivation cultivation) {
        checkModificationAccess(cultivation.getCrop());
        cultivation.getCrop().getCultivations().remove(cultivation);
        cultivationRepository.delete(cultivation);
        flushChanges();
    }

    @Override
    public Cultivation getCultivationById(UUID id) {
        return cultivationRepository.findById(id).orElse(Cultivation.NONE);
    }

    @Override
    public FertilizerApplication planFertilizerApplication(Crop crop, FertilizerApplication fertilizerApplication) {
        return createNewFertilizerApplication(crop, fertilizerApplication, true);
    }

    @Override
    public FertilizerApplication addFertilizerApplication(Crop crop, FertilizerApplication fertilizerApplication) {
        return createNewFertilizerApplication(crop, fertilizerApplication, false);
    }

    private FertilizerApplication createNewFertilizerApplication(Crop crop, FertilizerApplication fertilizerApplication, boolean planned) {
        checkModificationAccess(crop);
        Crop cropOriginal = cropRepository.findById(crop.getId()).orElse(MainCrop.NONE);
        if (cropOriginal.equals(MainCrop.NONE) || fertilizerApplication.getId() != null) {
            return FertilizerApplication.NONE;
        }
        if (fertilizerApplication.getFarmingMachine().equals(FarmingMachine.UNDEFINED)) {
            fertilizerApplication.setFarmingMachine(farmingMachineManager.getUndefinedFarmingMachine());
        }
        if (fertilizerApplication.getFertilizer().equals(Fertilizer.UNDEFINED) || fertilizerApplication.getFertilizer().getId() == null) {
            throw new IllegalArgumentException("No fertilizer found for this application");
        }

        if (fertilizerApplication.getFarmingMachine().getSupportedOperationTypes().contains(OperationType.FERTILIZER_APPLICATION) ||
                fertilizerApplication.getFarmingMachine().getSupportedOperationTypes().contains(OperationType.ANY)) {
            fertilizerApplication.setIsPlannedOperation(planned);
            fertilizerApplication.setCrop(cropOriginal);
            FertilizerApplication fertilizerApplicationSaved = fertilizerApplicationRepository.saveAndFlush(fertilizerApplication);
            cropOriginal.getFertilizerApplications().add(fertilizerApplicationSaved);
            flushChanges();
            return getFertilizerApplicationById(fertilizerApplicationSaved.getId());
        }
        throw new IllegalArgumentException("Fertilizer application is not possible using selected machine");
    }

    @Override
    public void deleteFertilizerApplication(FertilizerApplication fertilizerApplication) {
        checkModificationAccess(fertilizerApplication.getCrop());
        fertilizerApplication.getCrop().getFertilizerApplications().remove(fertilizerApplication);
        fertilizerApplicationRepository.delete(fertilizerApplication);
        flushChanges();
    }

    @Override
    public FertilizerApplication getFertilizerApplicationById(UUID id) {
        return fertilizerApplicationRepository.findById(id).orElse(FertilizerApplication.NONE);
    }

    @Override
    public SprayApplication planSprayApplication(Crop crop, SprayApplication sprayApplication) {
        return createNewSprayApplication(crop, sprayApplication, true);
    }

    @Override
    public SprayApplication addSprayApplication(Crop crop, SprayApplication sprayApplication) {
        return createNewSprayApplication(crop, sprayApplication, false);
    }

    private SprayApplication createNewSprayApplication(Crop crop, SprayApplication sprayApplication, boolean planned) {
        checkModificationAccess(crop);
        Crop cropOriginal = cropRepository.findById(crop.getId()).orElse(MainCrop.NONE);
        if (cropOriginal.equals(MainCrop.NONE) || sprayApplication.getId() != null) {
            return SprayApplication.NONE;
        }
        if (sprayApplication.getFarmingMachine().equals(FarmingMachine.UNDEFINED)) {
            sprayApplication.setFarmingMachine(farmingMachineManager.getUndefinedFarmingMachine());
        }
        if (sprayApplication.getSpray().equals(Spray.UNDEFINED) && sprayApplication.getFertilizer().equals(Fertilizer.UNDEFINED)) {
            throw new IllegalArgumentException("No spray or fertilizer found for this application");
        }
        if (sprayApplication.getSpray().getId() == null && sprayApplication.getFertilizer().getId() == null) {
            throw new IllegalArgumentException("No spray or fertilizer found for this application");
        }
        if (sprayApplication.getSpray().equals(Spray.UNDEFINED) || sprayApplication.getFertilizer().getId() == null) {
            sprayApplication.setSpray(sprayManager.getUndefinedSpray());
        }
        if (sprayApplication.getFertilizer().equals(Fertilizer.UNDEFINED) || sprayApplication.getFertilizer().getId() == null) {
            sprayApplication.setFertilizer(fertilizerManager.getUndefinedFertilizer());
        }

        if (sprayApplication.getFarmingMachine().getSupportedOperationTypes().contains(OperationType.SPRAY_APPLICATION) ||
                sprayApplication.getFarmingMachine().getSupportedOperationTypes().contains(OperationType.ANY)) {
            sprayApplication.setIsPlannedOperation(planned);
            sprayApplication.setCrop(cropOriginal);
            SprayApplication sprayApplicationSaved = sprayApplicationRepository.saveAndFlush(sprayApplication);
            cropOriginal.getSprayApplications().add(sprayApplicationSaved);
            flushChanges();
            return getSprayApplicationById(sprayApplicationSaved.getId());
        }
        throw new IllegalArgumentException("Spray application is not possible using selected machine");
    }

    @Override
    public void deleteSprayApplication(SprayApplication sprayApplication) {
        checkModificationAccess(sprayApplication.getCrop());
        sprayApplication.getCrop().getSprayApplications().remove(sprayApplication);
        sprayApplicationRepository.delete(sprayApplication);
        flushChanges();
    }

    @Override
    public SprayApplication getSprayApplicationById(UUID id) {
        return sprayApplicationRepository.findById(id).orElse(SprayApplication.NONE);
    }

    @Override
    public Harvest planHarvest(MainCrop crop, Harvest harvest) {
        return createNewHarvest(crop, harvest, true);
    }

    @Override
    public Harvest addHarvest(MainCrop crop, Harvest harvest) {
        return createNewHarvest(crop, harvest, false);
    }

    private Harvest createNewHarvest(MainCrop crop, Harvest harvest, boolean planned) {
        checkModificationAccess(crop);
        Crop cropOriginal = cropRepository.findById(crop.getId()).orElse(MainCrop.NONE);
        if (cropOriginal.equals(MainCrop.NONE) || harvest.getId() != null) {
            return Harvest.NONE;
        }
        if (harvest.getFarmingMachine().equals(FarmingMachine.UNDEFINED)) {
            harvest.setFarmingMachine(farmingMachineManager.getUndefinedFarmingMachine());
        }
        if (harvest.getCropParameters().equals(CropParameters.UNDEFINED) || harvest.getCropParameters().getId() == null) {
            harvest.setCropParameters(cropParametersManager.getUndefinedCropParameters());
        }

        if (harvest.getFarmingMachine().getSupportedOperationTypes().contains(OperationType.HARVEST) ||
                harvest.getFarmingMachine().getSupportedOperationTypes().contains(OperationType.ANY)) {
            harvest.setIsPlannedOperation(planned);
            harvest.setCrop(cropOriginal);
            Harvest harvestSaved = harvestRepository.saveAndFlush(harvest);
            ((MainCrop) cropOriginal).getHarvest().add(harvestSaved);
            flushChanges();
            return getHarvestById(harvestSaved.getId());
        }
        throw new IllegalArgumentException("Harvest is not possible using selected machine");
    }

    @Override
    public void deleteHarvest(Harvest harvest) {
        checkModificationAccess(harvest.getCrop());
        MainCrop crop = (MainCrop) harvest.getCrop();
        crop.getHarvest().remove(harvest);
        harvestRepository.delete(harvest);
        flushChanges();
    }

    @Override
    public Harvest getHarvestById(UUID id) {
        return harvestRepository.findById(id).orElse(Harvest.NONE);
    }

    @Override
    public Crop addSubside(Crop crop, Subside subside) {
        checkModificationAccess(crop);
        subside = subsideManager.getSubsideById(subside.getId());
        crop = getCropById(crop.getId());
        if (crop.getSubsides().contains(subside)) {
            return crop;
        }
        crop.getSubsides().add(subside);
        flushChanges();
        return getCropById(crop.getId());
    }

    @Override
    public Crop removeSubside(Crop crop, Subside subside) {
        checkModificationAccess(crop);
        subside = subsideManager.getSubsideById(subside.getId());
        crop = getCropById(crop.getId());
        crop.getSubsides().remove(subside);
        flushChanges();
        return getCropById(crop.getId());
    }

    @Override
    public CropSale addCropSale(MainCrop crop, CropSale cropSale) {
        checkSaleAccess(crop);
        Crop cropOriginal = cropRepository.findById(crop.getId()).orElse(MainCrop.NONE);
        if (cropOriginal.equals(MainCrop.NONE) || cropSale.getId() != null) {
            return CropSale.NONE;
        }
        if (cropSale.getCropParameters().equals(CropParameters.UNDEFINED) || cropSale.getCropParameters().getId() == null) {
            cropSale.setCropParameters(cropParametersManager.getUndefinedCropParameters());
        }
        cropSale.setCrop(cropOriginal);
        CropSale cropSaleSaved = cropSaleRepository.saveAndFlush(cropSale);
        ((MainCrop) cropOriginal).getCropSales().add(cropSaleSaved);
        flushChanges();
        return getCropSaleById(cropSaleSaved.getId());
    }

    @Override
    public CropSale updateCropSale(CropSale cropSale) {
        checkSaleAccess((MainCrop) cropSale.getCrop());
        CropSale cropSaleOriginal = cropSaleRepository.findById(cropSale.getId()).orElse(CropSale.NONE);
        if (cropSaleOriginal.equals(CropSale.NONE)) {
            return CropSale.NONE;
        }
        if (cropSale.getCropParameters().equals(CropParameters.UNDEFINED) || cropSale.getCropParameters().getId() == null) {
            cropSale.setCropParameters(cropParametersManager.getUndefinedCropParameters());
        }
        if (getCropById(cropSale.getCrop().getId()).equals(cropSaleOriginal.getCrop())) {
            return cropSaleRepository.saveAndFlush(cropSale);
        }
        return CropSale.NONE;
    }

    @Override
    public void removeCropSale(CropSale cropSale) {
        checkSaleAccess((MainCrop) cropSale.getCrop());
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
