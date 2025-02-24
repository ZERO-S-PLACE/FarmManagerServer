package org.zeros.farm_manager_server.services.default_impl.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.domain.dto.operations.HarvestDTO;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.crop.MainCrop;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.operations.Harvest;
import org.zeros.farm_manager_server.domain.enums.OperationType;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.repositories.operations.HarvestRepository;
import org.zeros.farm_manager_server.services.interfaces.crop.CropManager;
import org.zeros.farm_manager_server.services.interfaces.crop.CropParametersManager;
import org.zeros.farm_manager_server.services.interfaces.data.FarmingMachineManager;
import org.zeros.farm_manager_server.services.interfaces.operations.OperationManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class HarvestManagerDefault implements OperationManager<Harvest, HarvestDTO> {
    private final HarvestRepository harvestRepository;
    private final FarmingMachineManager farmingMachineManager;
    private final CropManager cropManager;
    private final CropParametersManager cropParametersManager;

    @Override
    @Transactional(readOnly = true)
    public Harvest getOperationById(UUID id) {

        return harvestRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(
                Harvest.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }

    @Override
    @Transactional
    public Harvest planOperation(UUID cropId, HarvestDTO harvestDTO) {
        return createNewHarvest(cropId, harvestDTO, true);
    }

    @Override
    @Transactional
    public Harvest addOperation(UUID cropId, HarvestDTO harvestDTO) {
        return createNewHarvest(cropId, harvestDTO, false);
    }

    @Override
    @Transactional
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
    @Transactional
    public void deleteOperation(UUID operationId) {
        Harvest harvest = getOperationById(operationId);
        if (harvest == Harvest.NONE) {
            return;
        }
        checkOperationModificationAccess(harvest.getCrop());
        harvestRepository.delete(harvest);
    }

    @Transactional
    protected Harvest createNewHarvest(UUID cropId, HarvestDTO operationDTO, boolean planned) {
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
        ((MainCrop) crop).getHarvest().add(harvestSaved);

        return getOperationById(harvestSaved.getId());

    }

    private void checkIfMainCrop(Crop crop) {
        if (crop instanceof MainCrop) {
            return;
        }
        throw new IllegalArgumentExceptionCustom(MainCrop.class, IllegalArgumentExceptionCause.TYPE_MISMATCH);
    }

    private Harvest rewriteToEntity(HarvestDTO dto, Harvest entity) {
        Harvest entityParsed = DefaultMappers.harvestMapper.dtoToEntitySimpleProperties(dto);
        entityParsed= (Harvest) rewriteNotModifiedParameters(entity, entityParsed);
        entityParsed.setCropParameters(cropParametersManager.getCropParametersIfExist(dto.getCropParameters()));
        entityParsed.setOperationType(OperationType.HARVEST);
        return entityParsed;
    }
}
