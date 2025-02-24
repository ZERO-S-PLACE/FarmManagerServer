package org.zeros.farm_manager_server.services.default_impl.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.domain.dto.operations.*;
import org.zeros.farm_manager_server.domain.entities.data.FarmingMachine;
import org.zeros.farm_manager_server.domain.entities.operations.*;
import org.zeros.farm_manager_server.domain.enums.OperationType;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.services.interfaces.data.FarmingMachineManager;
import org.zeros.farm_manager_server.services.interfaces.operations.AgriculturalOperationsManager;
import org.zeros.farm_manager_server.services.interfaces.operations.OperationManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class AgriculturalOperationsManagerDefault implements AgriculturalOperationsManager {
    private final FarmingMachineManager farmingMachineManager;
    private final OperationManager<Seeding, SeedingDTO> seedingManager;
    private final OperationManager<Cultivation, CultivationDTO> cultivationManager;
    private final OperationManager<FertilizerApplication, FertilizerApplicationDTO> fertilizerApplicationManager;
    private final OperationManager<SprayApplication, SprayApplicationDTO> sprayApplicationManager;
    private final OperationManager<Harvest, HarvestDTO> harvestManager;

    @Override
    @Transactional
    public AgriculturalOperationDTO getOperationById(UUID operationId, OperationType operationType) {
        AgriculturalOperation operation = getOperationEntityById(operationId, operationType);
        return DefaultMappers.agriculturalOperationMapper.entityToDto(operation);
    }

    private AgriculturalOperation getOperationEntityById(UUID operationId, OperationType operationType) {
        return switch (operationType) {
            case CULTIVATION -> cultivationManager.getOperationById(operationId);
            case SEEDING -> seedingManager.getOperationById(operationId);
            case FERTILIZER_APPLICATION -> fertilizerApplicationManager.getOperationById(operationId);
            case SPRAY_APPLICATION -> sprayApplicationManager.getOperationById(operationId);
            case HARVEST -> harvestManager.getOperationById(operationId);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
    }

    @Override
    @Transactional
    public AgriculturalOperationDTO planOperation(UUID cropId, AgriculturalOperationDTO operationDTO) {
        AgriculturalOperation operation = switch (operationDTO) {
            case SeedingDTO seedingDTO -> seedingManager.planOperation(cropId, seedingDTO);
            case CultivationDTO cultivationDTO -> cultivationManager.planOperation(cropId, cultivationDTO);
            case FertilizerApplicationDTO fertilizerApplicationDTO ->
                    fertilizerApplicationManager.planOperation(cropId, fertilizerApplicationDTO);
            case SprayApplicationDTO sprayApplicationDTO ->
                    sprayApplicationManager.planOperation(cropId, sprayApplicationDTO);
            case HarvestDTO harvestDTO -> harvestManager.planOperation(cropId, harvestDTO);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
        return DefaultMappers.agriculturalOperationMapper.entityToDto(operation);
    }

    @Override
    @Transactional
    public AgriculturalOperationDTO addOperation(UUID cropId, AgriculturalOperationDTO operationDTO) {
        AgriculturalOperation operation = switch (operationDTO) {
            case SeedingDTO seedingDTO -> seedingManager.addOperation(cropId, seedingDTO);
            case CultivationDTO cultivationDTO -> cultivationManager.addOperation(cropId, cultivationDTO);
            case FertilizerApplicationDTO fertilizerApplicationDTO ->
                    fertilizerApplicationManager.addOperation(cropId, fertilizerApplicationDTO);
            case SprayApplicationDTO sprayApplicationDTO ->
                    sprayApplicationManager.addOperation(cropId, sprayApplicationDTO);
            case HarvestDTO harvestDTO -> harvestManager.addOperation(cropId, harvestDTO);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
        return DefaultMappers.agriculturalOperationMapper.entityToDto(operation);
    }


    @Override
    @Transactional
    public void setPlannedOperationPerformed(UUID operationId, OperationType operationType) {
        AgriculturalOperation operation = getOperationEntityById(operationId, operationType);
        operation.setIsPlannedOperation(false);

    }

    @Override
    @Transactional
    public void updateOperationMachine(UUID operationId, OperationType operationType, UUID farmingMachineId) {
        AgriculturalOperation operation = getOperationEntityById(operationId, operationType);
        FarmingMachine farmingMachine = farmingMachineManager.getFarmingMachineIfCompatible(
                farmingMachineId, operationType);
        operation.setFarmingMachine(farmingMachine);

    }


    @Override
    @Transactional
    public void updateOperationParameters(AgriculturalOperationDTO operationDTO) {
        switch (operationDTO) {
            case SeedingDTO seedingDTO -> seedingManager.updateOperation(seedingDTO);
            case CultivationDTO cultivationDTO -> cultivationManager.updateOperation(cultivationDTO);
            case FertilizerApplicationDTO fertilizerApplicationDTO ->
                    fertilizerApplicationManager.updateOperation(fertilizerApplicationDTO);
            case SprayApplicationDTO sprayApplicationDTO ->
                    sprayApplicationManager.updateOperation(sprayApplicationDTO);
            case HarvestDTO harvestDTO -> harvestManager.updateOperation(harvestDTO);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        }

    }

    @Override
    @Transactional
    public void deleteOperation(UUID operationId, OperationType operationType) {
        switch (operationType) {
            case CULTIVATION -> cultivationManager.deleteOperation(operationId);
            case SEEDING -> seedingManager.deleteOperation(operationId);
            case FERTILIZER_APPLICATION -> fertilizerApplicationManager.deleteOperation(operationId);
            case SPRAY_APPLICATION -> sprayApplicationManager.deleteOperation(operationId);
            case HARVEST -> harvestManager.deleteOperation(operationId);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        }

    }
}
