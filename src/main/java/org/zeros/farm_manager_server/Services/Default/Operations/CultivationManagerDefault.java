package org.zeros.farm_manager_server.Services.Default.Operations;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Domain.DTO.Operations.CultivationDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.Operations.Cultivation;
import org.zeros.farm_manager_server.Domain.Entities.Operations.Seeding;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.CultivationRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.OperationManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class CultivationManagerDefault implements OperationManager<Cultivation, CultivationDTO> {
    private final EntityManager entityManager;
    private final CultivationRepository cultivationRepository;
    private final FarmingMachineManager farmingMachineManager;
    private final CropManager cropManager;

    @Override
    @Transactional(readOnly = true)
    public Cultivation getOperationById(UUID id) {

        return cultivationRepository.findById(id).orElseThrow(()->new IllegalArgumentExceptionCustom(
                Cultivation.class,IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }

    @Override
    @Transactional
    public Cultivation planOperation(UUID cropId, CultivationDTO cultivationDTO) {
        return createNewCultivation(cropId, cultivationDTO, true);
    }

    @Override
    @Transactional
    public Cultivation addOperation(UUID cropId, CultivationDTO cultivationDTO) {
        return createNewCultivation(cropId, cultivationDTO, false);
    }

    @Override
    @Transactional
    public Cultivation updateOperation(CultivationDTO cultivationDTO) {
        Crop crop = cropManager.getCropIfExists(cultivationDTO.getCrop());
        checkOperationModificationAccess(crop);
        Cultivation cultivationOriginal = getCultivationIfExist(cultivationDTO.getId());
        return cultivationRepository.saveAndFlush(rewriteToEntity(cultivationDTO, cultivationOriginal));
    }

    private Cultivation getCultivationIfExist(UUID id) {
        if (id == null) {
            throw new IllegalArgumentExceptionCustom(Cultivation.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return cultivationRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(
                Cultivation.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));

    }

    @Override
    @Transactional
    public void deleteOperation(UUID operationId) {
        Cultivation cultivation = getOperationById(operationId);
        if (cultivation == Cultivation.NONE) {
            return;
        }
        checkOperationModificationAccess(cultivation.getCrop());
        cultivationRepository.delete(cultivation);
    }

    @Transactional
    protected Cultivation createNewCultivation(UUID cropId, CultivationDTO operationDTO, boolean planned) {
        Crop crop = cropManager.getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        checkIfUUIDPresent(operationDTO);
        Cultivation cultivation = rewriteToEntity(operationDTO, Cultivation.NONE);
        FarmingMachine farmingMachine = farmingMachineManager.getFarmingMachineIfCompatible(
                operationDTO.getFarmingMachine(), OperationType.CULTIVATION);
        cultivation.setFarmingMachine(farmingMachine);
        cultivation.setIsPlannedOperation(planned);
        cultivation.setCrop(crop);
        Cultivation cultivationSaved = cultivationRepository.saveAndFlush(cultivation);
        crop.getCultivations().add(cultivationSaved);
        flushChanges();
        return getOperationById(cultivationSaved.getId());

    }

    private Cultivation rewriteToEntity(CultivationDTO dto, Cultivation entity) {
        Cultivation entityParsed = DefaultMappers.cultivationMapper.dtoToEntitySimpleProperties(dto);
        rewriteNotModifiedParameters(entity, entityParsed);
        return entityParsed;
    }

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }
}
