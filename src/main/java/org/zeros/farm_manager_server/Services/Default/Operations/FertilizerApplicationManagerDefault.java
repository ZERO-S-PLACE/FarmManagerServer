package org.zeros.farm_manager_server.Services.Default.Operations;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Domain.DTO.Operations.FertilizerApplicationDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.Operations.FertilizerApplication;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.FertilizerApplicationRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.OperationManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class FertilizerApplicationManagerDefault implements OperationManager<FertilizerApplication, FertilizerApplicationDTO> {
    private final EntityManager entityManager;
    private final FertilizerApplicationRepository fertilizerApplicationRepository;
    private final FarmingMachineManager farmingMachineManager;
    private final CropManager cropManager;
    private final FertilizerManager fertilizerManager;

    @Override
    public FertilizerApplication getOperationById(UUID id) {
        return fertilizerApplicationRepository.findById(id).orElse(FertilizerApplication.NONE);
    }

    @Override
    public FertilizerApplication planOperation(UUID cropId, FertilizerApplicationDTO fertilizerApplicationDTO) {
        return createNewFertilizerApplication(cropId, fertilizerApplicationDTO, true);
    }

    @Override
    public FertilizerApplication addOperation(UUID cropId, FertilizerApplicationDTO fertilizerApplicationDTO) {
        return createNewFertilizerApplication(cropId, fertilizerApplicationDTO, false);
    }

    @Override
    public FertilizerApplication updateOperation(FertilizerApplicationDTO fertilizerApplicationDTO) {
        Crop crop = cropManager.getCropIfExists(fertilizerApplicationDTO.getCrop());
        checkOperationModificationAccess(crop);
        FertilizerApplication fertilizerApplicationOriginal = getFertilizerApplicationIfExist(fertilizerApplicationDTO.getId());
        return fertilizerApplicationRepository.saveAndFlush(rewriteToEntity(fertilizerApplicationDTO, fertilizerApplicationOriginal));
    }

    private FertilizerApplication getFertilizerApplicationIfExist(UUID id) {
        if (id == null) {
            throw new IllegalArgumentExceptionCustom(FertilizerApplication.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return fertilizerApplicationRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(
                FertilizerApplication.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));

    }

    @Override
    public void deleteOperation(UUID operationId) {
        FertilizerApplication fertilizerApplication = getOperationById(operationId);
        if (fertilizerApplication == FertilizerApplication.NONE) {
            return;
        }
        checkOperationModificationAccess(fertilizerApplication.getCrop());
        fertilizerApplicationRepository.delete(fertilizerApplication);
    }

    private FertilizerApplication createNewFertilizerApplication(UUID cropId, FertilizerApplicationDTO operationDTO, boolean planned) {
        Crop crop = cropManager.getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        checkIfUUIDPresent(operationDTO);
        FertilizerApplication fertilizerApplication = rewriteToEntity(operationDTO, FertilizerApplication.NONE);
        FarmingMachine farmingMachine = farmingMachineManager.getFarmingMachineIfCompatible(
                operationDTO.getFarmingMachine(), OperationType.FERTILIZER_APPLICATION);
        fertilizerApplication.setFarmingMachine(farmingMachine);
        fertilizerApplication.setIsPlannedOperation(planned);
        fertilizerApplication.setCrop(crop);
        FertilizerApplication fertilizerApplicationSaved = fertilizerApplicationRepository.saveAndFlush(fertilizerApplication);
        crop.getFertilizerApplications().add(fertilizerApplicationSaved);
        flushChanges();
        return getOperationById(fertilizerApplicationSaved.getId());

    }

    private FertilizerApplication rewriteToEntity(FertilizerApplicationDTO dto, FertilizerApplication entity) {
        FertilizerApplication entityParsed = DefaultMappers.fertilizerApplicationMapper.dtoToEntitySimpleProperties(dto);
        rewriteNotModifiedParameters(entity, entityParsed);
        entityParsed.setFertilizer(fertilizerManager.getFertilizerIfExists(dto.getFertilizer()));
        return entityParsed;
    }

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }
}
