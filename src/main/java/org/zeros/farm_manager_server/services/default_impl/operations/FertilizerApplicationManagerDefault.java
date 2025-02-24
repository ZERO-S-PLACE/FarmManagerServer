package org.zeros.farm_manager_server.services.default_impl.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.domain.dto.operations.FertilizerApplicationDTO;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.operations.FertilizerApplication;
import org.zeros.farm_manager_server.domain.enums.OperationType;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.repositories.operations.FertilizerApplicationRepository;
import org.zeros.farm_manager_server.services.interfaces.crop.CropManager;
import org.zeros.farm_manager_server.services.interfaces.data.FarmingMachineManager;
import org.zeros.farm_manager_server.services.interfaces.data.FertilizerManager;
import org.zeros.farm_manager_server.services.interfaces.operations.OperationManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class FertilizerApplicationManagerDefault implements OperationManager<FertilizerApplication, FertilizerApplicationDTO> {
    private final FertilizerApplicationRepository fertilizerApplicationRepository;
    private final FarmingMachineManager farmingMachineManager;
    private final CropManager cropManager;
    private final FertilizerManager fertilizerManager;

    @Override
    @Transactional(readOnly = true)
    public FertilizerApplication getOperationById(UUID id) {
        return fertilizerApplicationRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(
                FertilizerApplication.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }

    @Override
    @Transactional
    public FertilizerApplication planOperation(UUID cropId, FertilizerApplicationDTO fertilizerApplicationDTO) {
        return createNewFertilizerApplication(cropId, fertilizerApplicationDTO, true);
    }

    @Override
    @Transactional
    public FertilizerApplication addOperation(UUID cropId, FertilizerApplicationDTO fertilizerApplicationDTO) {
        return createNewFertilizerApplication(cropId, fertilizerApplicationDTO, false);
    }

    @Override
    @Transactional
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
    @Transactional
    public void deleteOperation(UUID operationId) {
        FertilizerApplication fertilizerApplication = getOperationById(operationId);
        if (fertilizerApplication == FertilizerApplication.NONE) {
            return;
        }
        checkOperationModificationAccess(fertilizerApplication.getCrop());
        fertilizerApplicationRepository.delete(fertilizerApplication);
    }

    @Transactional
    protected FertilizerApplication createNewFertilizerApplication(UUID cropId, FertilizerApplicationDTO operationDTO, boolean planned) {
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

        return getOperationById(fertilizerApplicationSaved.getId());

    }

    private FertilizerApplication rewriteToEntity(FertilizerApplicationDTO dto, FertilizerApplication entity) {
        FertilizerApplication entityParsed = DefaultMappers.fertilizerApplicationMapper.dtoToEntitySimpleProperties(dto);
        entityParsed= (FertilizerApplication) rewriteNotModifiedParameters(entity, entityParsed);
        entityParsed.setFertilizer(fertilizerManager.getFertilizerIfExists(dto.getFertilizer()));
        entityParsed.setOperationType(OperationType.SEEDING);
        return entityParsed;
    }
}
