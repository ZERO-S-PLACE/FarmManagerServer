package org.zeros.farm_manager_server.Services.Default.Operations;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Domain.DTO.Operations.HarvestDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.Operations.Harvest;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.HarvestRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.CropParameters.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.OperationManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class HarvestManagerDefault implements OperationManager<Harvest, HarvestDTO> {
    private final EntityManager entityManager;
    private final HarvestRepository harvestRepository;
    private final FarmingMachineManager farmingMachineManager;
    private final CropManager cropManager;
    private final CropParametersManager cropParametersManager;

    @Override
    public Harvest getOperationById(UUID id) {
        return harvestRepository.findById(id).orElse(Harvest.NONE);
    }

    @Override
    public Harvest planOperation(UUID cropId, HarvestDTO harvestDTO) {
        return createNewHarvest(cropId, harvestDTO, true);
    }

    @Override
    public Harvest addOperation(UUID cropId, HarvestDTO harvestDTO) {
        return createNewHarvest(cropId, harvestDTO, false);
    }

    @Override
    public Harvest updateOperation(HarvestDTO harvestDTO) {
        Crop crop = cropManager.getCropIfExists(harvestDTO.getCrop());
        checkOperationModificationAccess(crop);
        Harvest harvestOriginal = getHarvestIfExist(harvestDTO.getId());
        return harvestRepository.saveAndFlush(rewriteToEntity(harvestDTO, harvestOriginal));
    }

    private Harvest getHarvestIfExist(UUID id) {
        if (id == null) {
            throw new IllegalArgumentExceptionCustom(Harvest.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return harvestRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(
                Harvest.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));

    }

    @Override
    public void deleteOperation(UUID operationId) {
        Harvest harvest = getOperationById(operationId);
        if (harvest == Harvest.NONE) {
            return;
        }
        checkOperationModificationAccess(harvest.getCrop());
        harvestRepository.delete(harvest);
    }

    private Harvest createNewHarvest(UUID cropId, HarvestDTO operationDTO, boolean planned) {
        Crop crop = cropManager.getCropIfExists(cropId);
        checkIfMainCrop(crop);
        checkOperationModificationAccess(crop);
        checkIfUUIDPresent(operationDTO);
        Harvest harvest = rewriteToEntity(operationDTO, Harvest.NONE);
        FarmingMachine farmingMachine = farmingMachineManager.getFarmingMachineIfCompatible(
                operationDTO.getFarmingMachine(), OperationType.HARVEST);
        harvest.setFarmingMachine(farmingMachine);
        harvest.setIsPlannedOperation(planned);
        harvest.setCrop(crop);
        Harvest harvestSaved = harvestRepository.saveAndFlush(harvest);
        ((MainCrop)crop).getHarvest().add(harvestSaved);
        flushChanges();
        return getOperationById(harvestSaved.getId());

    }

    private void checkIfMainCrop(Crop crop) {
        if (crop instanceof MainCrop){return;}
        throw new IllegalArgumentExceptionCustom(MainCrop.class,IllegalArgumentExceptionCause.TYPE_MISMATCH);
    }

    private Harvest rewriteToEntity(HarvestDTO dto, Harvest entity) {
        Harvest entityParsed = DefaultMappers.harvestMapper.dtoToEntitySimpleProperties(dto);
        rewriteNotModifiedParameters(entity, entityParsed);
        entityParsed.setCropParameters(cropParametersManager.getCropParametersIfExist(dto.getCropParameters()));
        return entityParsed;
    }

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }
}
