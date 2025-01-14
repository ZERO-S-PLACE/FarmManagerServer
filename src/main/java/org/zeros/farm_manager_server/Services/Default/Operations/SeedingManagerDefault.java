package org.zeros.farm_manager_server.Services.Default.Operations;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Domain.DTO.Operations.SeedingDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.Operations.Seeding;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.SeedingRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Services.Interface.Data.PlantManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.OperationManager;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class SeedingManagerDefault implements OperationManager<Seeding, SeedingDTO> {
    private final EntityManager entityManager;
    private final SeedingRepository seedingRepository;
    private final PlantManager plantManager;
    private final FarmingMachineManager farmingMachineManager;
    private final CropManager cropManager;

    @Override
    @Transactional(readOnly = true)
    public Seeding getOperationById(UUID id) {
        return seedingRepository.findById(id).orElse(Seeding.NONE);
    }

    @Override
    @Transactional
    public Seeding planOperation(UUID cropId, SeedingDTO seedingDTO) {
        return createNewSeeding(cropId, seedingDTO, true);
    }

    @Override
    @Transactional
    public Seeding addOperation(UUID cropId, SeedingDTO seedingDTO) {
        return createNewSeeding(cropId, seedingDTO, false);
    }

    @Override
    @Transactional
    public Seeding updateOperation(SeedingDTO seedingDTO) {
        Crop crop = cropManager.getCropIfExists(seedingDTO.getCrop());
        checkOperationModificationAccess(crop);
        Seeding seedingOriginal = getSeedingIfExist(seedingDTO.getId());
        return seedingRepository.saveAndFlush(rewriteToEntity(seedingDTO, seedingOriginal));
    }

    private Seeding getSeedingIfExist(UUID id) {
        if (id == null) {
            throw new IllegalArgumentExceptionCustom(Seeding.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return seedingRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(
                Seeding.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));

    }

    @Override
    @Transactional
    public void deleteOperation(UUID operationId) {
        Seeding seeding = getOperationById(operationId);
        if (seeding == Seeding.NONE) {
            return;
        }
        checkOperationModificationAccess(seeding.getCrop());
        seedingRepository.delete(seeding);
    }

    @Transactional
    protected Seeding createNewSeeding(UUID cropId, SeedingDTO operationDTO, boolean planned) {
        Crop crop = cropManager.getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        checkIfUUIDPresent(operationDTO);
        Seeding seeding = rewriteToEntity(operationDTO, Seeding.NONE);
        FarmingMachine farmingMachine = farmingMachineManager.getFarmingMachineIfCompatible(
                operationDTO.getFarmingMachine(), OperationType.SEEDING);
        seeding.setFarmingMachine(farmingMachine);
        seeding.setIsPlannedOperation(planned);
        seeding.setCrop(crop);
        Seeding seedingSaved = seedingRepository.saveAndFlush(seeding);
        crop.getSeeding().add(seedingSaved);
        flushChanges();
        return getOperationById(seedingSaved.getId());

    }

    private Seeding rewriteToEntity(SeedingDTO dto, Seeding entity) {
        Seeding entityParsed = DefaultMappers.seedingMapper.dtoToEntitySimpleProperties(dto);
        rewriteNotModifiedParameters(entity, entityParsed);
        entityParsed.setSownPlants(dto.getSownPlants().stream().map(plantManager::getPlantIfExists).collect(Collectors.toSet()));
        return entityParsed;
    }

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }
}
