package org.zeros.farm_manager_server.Services.Default.Operations;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Domain.DTO.Operations.SprayApplicationDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.Operations.Cultivation;
import org.zeros.farm_manager_server.Domain.Entities.Operations.SprayApplication;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.SprayApplicationRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;
import org.zeros.farm_manager_server.Services.Interface.Data.SprayManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.OperationManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class SprayApplicationManagerDefault implements OperationManager<SprayApplication, SprayApplicationDTO> {
    private final EntityManager entityManager;
    private final SprayApplicationRepository sprayApplicationRepository;
    private final FarmingMachineManager farmingMachineManager;
    private final CropManager cropManager;
    private final SprayManager sprayManager;
    private final FertilizerManager fertilizerManager;

    @Override
    @Transactional(readOnly = true)
    public SprayApplication getOperationById(UUID id) {
        return sprayApplicationRepository.findById(id).orElseThrow(()->new IllegalArgumentExceptionCustom(
                SprayApplication.class,IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
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
        flushChanges();
        return getOperationById(sprayApplicationSaved.getId());

    }

    private SprayApplication rewriteToEntity(SprayApplicationDTO dto, SprayApplication entity) {
        SprayApplication entityParsed = DefaultMappers.sprayApplicationMapper.dtoToEntitySimpleProperties(dto);
        rewriteNotModifiedParameters(entity, entityParsed);
        entityParsed.setFertilizer(fertilizerManager.getFertilizerIfExists(dto.getFertilizer()));
        entityParsed.setSpray(sprayManager.getSprayIfExists(dto.getSpray()));
        return entityParsed;
    }

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }
}
