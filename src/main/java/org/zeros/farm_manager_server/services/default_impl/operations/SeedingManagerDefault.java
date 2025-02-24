package org.zeros.farm_manager_server.services.default_impl.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.domain.dto.operations.SeedingDTO;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.operations.Seeding;
import org.zeros.farm_manager_server.domain.enums.OperationType;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.repositories.operations.SeedingRepository;
import org.zeros.farm_manager_server.services.interfaces.crop.CropManager;
import org.zeros.farm_manager_server.services.interfaces.data.FarmingMachineManager;
import org.zeros.farm_manager_server.services.interfaces.data.PlantManager;
import org.zeros.farm_manager_server.services.interfaces.operations.OperationManager;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class SeedingManagerDefault implements OperationManager<Seeding, SeedingDTO> {
    private final SeedingRepository seedingRepository;
    private final PlantManager plantManager;
    private final FarmingMachineManager farmingMachineManager;
    private final CropManager cropManager;

    @Override
    @Transactional(readOnly = true)
    public Seeding getOperationById(UUID id) {
        return seedingRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(
                Seeding.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
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
        return seedingRepository.save(rewriteToEntity(seedingDTO, seedingOriginal));
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

        Seeding seedingSaved = seedingRepository.save(seeding);
        crop.getSeeding().add(seedingSaved);
        return getOperationById(seedingSaved.getId());
    }

    private Seeding rewriteToEntity(SeedingDTO dto, Seeding entity) {
        Seeding entityParsed = DefaultMappers.seedingMapper.dtoToEntitySimpleProperties(dto);
        entityParsed= (Seeding) rewriteNotModifiedParameters(entity, entityParsed);
        entityParsed.setSownPlants(dto.getSownPlants().stream().map(plantManager::getPlantIfExists).collect(Collectors.toSet()));
        entityParsed.setOperationType(OperationType.SEEDING);
        return entityParsed;
    }
}
