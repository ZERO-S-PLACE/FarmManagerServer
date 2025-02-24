package org.zeros.farm_manager_server.services.default_impl.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.domain.dto.operations.SprayApplicationDTO;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.operations.SprayApplication;
import org.zeros.farm_manager_server.domain.enums.OperationType;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.repositories.operations.SprayApplicationRepository;
import org.zeros.farm_manager_server.services.interfaces.crop.CropManager;
import org.zeros.farm_manager_server.services.interfaces.data.FarmingMachineManager;
import org.zeros.farm_manager_server.services.interfaces.data.FertilizerManager;
import org.zeros.farm_manager_server.services.interfaces.data.SprayManager;
import org.zeros.farm_manager_server.services.interfaces.operations.OperationManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class SprayApplicationManagerDefault implements OperationManager<SprayApplication, SprayApplicationDTO> {
    private final SprayApplicationRepository sprayApplicationRepository;
    private final FarmingMachineManager farmingMachineManager;
    private final CropManager cropManager;
    private final SprayManager sprayManager;
    private final FertilizerManager fertilizerManager;

    @Override
    @Transactional(readOnly = true)
    public SprayApplication getOperationById(UUID id) {
        return sprayApplicationRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(
                SprayApplication.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }

    @Override
    @Transactional
    public SprayApplication planOperation(UUID cropId, SprayApplicationDTO sprayApplicationDTO) {
        return createNewSprayApplication(cropId, sprayApplicationDTO, true);
    }

    @Override
    @Transactional
    public SprayApplication addOperation(UUID cropId, SprayApplicationDTO sprayApplicationDTO) {
        return createNewSprayApplication(cropId, sprayApplicationDTO, false);
    }

    @Override
    @Transactional
    public SprayApplication updateOperation(SprayApplicationDTO sprayApplicationDTO) {
        Crop crop = cropManager.getCropIfExists(sprayApplicationDTO.getCrop());
        checkOperationModificationAccess(crop);
        SprayApplication sprayApplicationOriginal = getSprayApplicationIfExist(sprayApplicationDTO.getId());
        return sprayApplicationRepository.saveAndFlush(rewriteToEntity(sprayApplicationDTO, sprayApplicationOriginal));
    }

    private SprayApplication getSprayApplicationIfExist(UUID id) {
        if (id == null) {
            throw new IllegalArgumentExceptionCustom(SprayApplication.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return sprayApplicationRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(
                SprayApplication.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));

    }

    @Override
    @Transactional
    public void deleteOperation(UUID operationId) {
        SprayApplication sprayApplication = getOperationById(operationId);
        if (sprayApplication == SprayApplication.NONE) {
            return;
        }
        checkOperationModificationAccess(sprayApplication.getCrop());
        sprayApplicationRepository.delete(sprayApplication);
    }

    @Transactional
    protected SprayApplication createNewSprayApplication(UUID cropId, SprayApplicationDTO operationDTO, boolean planned) {
        Crop crop = cropManager.getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        checkIfUUIDPresent(operationDTO);
        SprayApplication sprayApplication = rewriteToEntity(operationDTO, SprayApplication.NONE);
        FarmingMachine farmingMachine = farmingMachineManager.getFarmingMachineIfCompatible(
                operationDTO.getFarmingMachine(), OperationType.SPRAY_APPLICATION);
        sprayApplication.setFarmingMachine(farmingMachine);
        sprayApplication.setIsPlannedOperation(planned);
        sprayApplication.setCrop(crop);
        SprayApplication sprayApplicationSaved = sprayApplicationRepository.saveAndFlush(sprayApplication);
        crop.getSprayApplications().add(sprayApplicationSaved);

        return getOperationById(sprayApplicationSaved.getId());

    }

    private SprayApplication rewriteToEntity(SprayApplicationDTO dto, SprayApplication entity) {
        SprayApplication entityParsed = DefaultMappers.sprayApplicationMapper.dtoToEntitySimpleProperties(dto);
        entityParsed= (SprayApplication) rewriteNotModifiedParameters(entity, entityParsed);
        entityParsed.setFertilizer(fertilizerManager.getFertilizerIfExists(dto.getFertilizer()));
        entityParsed.setSpray(sprayManager.getSprayIfExists(dto.getSpray()));
        entityParsed.setOperationType(OperationType.SPRAY_APPLICATION);
        return entityParsed;
    }
}
