package org.zeros.farm_manager_server.Services.Default.Operations;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Domain.DTO.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.Operations.*;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.AgriculturalOperationsManager;
import org.zeros.farm_manager_server.Services.Interface.Operations.OperationManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class AgriculturalOperationsManagerDefault implements AgriculturalOperationsManager {
    private final EntityManager entityManager;
    private final FarmingMachineManager farmingMachineManager;
    private final OperationManager<Seeding, SeedingDTO> seedingManager;
    private final OperationManager<Cultivation, CultivationDTO> cultivationManager;
    private final OperationManager<FertilizerApplication, FertilizerApplicationDTO> fertilizerApplicationManager;
    private final OperationManager<SprayApplication, SprayApplicationDTO> sprayApplicationManager;
    private final OperationManager<Harvest, HarvestDTO> harvestManager;

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }


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
        flushChanges();
    }

    @Override
    @Transactional
    public void updateOperationMachine(UUID operationId, OperationType operationType, UUID farmingMachineId) {
        AgriculturalOperation operation = getOperationEntityById(operationId, operationType);
        FarmingMachine farmingMachine = farmingMachineManager.getFarmingMachineIfCompatible(
                farmingMachineId, operationType);
        operation.setFarmingMachine(farmingMachine);
        flushChanges();
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
        flushChanges();
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
        flushChanges();
    }
}
