package org.zeros.farm_manager_server.Services.Default.Data;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.*;
import org.zeros.farm_manager_server.Repositories.Data.FarmingMachineRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;

import java.util.Set;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class FarmingMachineManagerDefault implements FarmingMachineManager {
    private final LoggedUserConfiguration config;
    private final FarmingMachineRepository farmingMachineRepository;
    private final SeedingRepository seedingRepository;
    private final CultivationRepository cultivationRepository;
    private final SprayApplicationRepository sprayApplicationRepository;
    private final FertilizerApplicationRepository fertilizerApplicationRepository;
    private final HarvestRepository harvestRepository;

    private static PageRequest getPageRequest(int pageNumber) {
        if (pageNumber < 0) pageNumber = 0;
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmingMachineDTO> getAllFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(config.allRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.farmingMachineMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmingMachineDTO> getDefaultFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(config.defaultRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.farmingMachineMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmingMachineDTO> getUserFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(config.userRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.farmingMachineMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmingMachineDTO> getFarmingMachineByNameAs(String model, int pageNumber) {
        return farmingMachineRepository.findAllByModelContainingIgnoreCaseAndCreatedByIn(model,
                        config.allRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.farmingMachineMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmingMachineDTO> getFarmingMachineByProducerAs(String producer, int pageNumber) {
        return farmingMachineRepository.findAllByProducerContainingIgnoreCaseAndCreatedByIn(producer,
                        config.allRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.farmingMachineMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmingMachineDTO> getFarmingMachineByProducerAndNameAs(String producer,
                                                                        String model, int pageNumber) {
        return farmingMachineRepository
                .findAllByProducerContainingIgnoreCaseAndModelContainingIgnoreCaseAndCreatedByIn(
                        producer, model, config.allRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.farmingMachineMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmingMachineDTO> getFarmingMachineBySupportedOperation(OperationType operationType,
                                                                         int pageNumber) {
        return farmingMachineRepository.findAllBySupportedOperationTypesContainsAndCreatedByIn(
                        operationType, config.allRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.farmingMachineMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmingMachineDTO> getFarmingMachineCriteria(String model, String producer,
                                                             OperationType operationType, int pageNumber) {
        boolean modelPresent = !(model == null || model.isBlank());
        boolean producerPresent = !(producer == null || producer.isBlank());
        boolean operationTypePresent = !(operationType == null || operationType == OperationType.ANY);

        if (modelPresent && producerPresent) {
            return getFarmingMachineByProducerAndNameAs(producer, model, pageNumber);
        }
        if (modelPresent) {
            return getFarmingMachineByNameAs(model, pageNumber);
        }
        if (producerPresent) {
            return getFarmingMachineByProducerAs(producer, pageNumber);
        }
        if (operationTypePresent) {
            return getFarmingMachineBySupportedOperation(operationType, pageNumber);
        }
        return getAllFarmingMachines(pageNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmingMachineDTO getFarmingMachineById(UUID id) {
        return DefaultMappers.farmingMachineMapper.entityToDto(
                farmingMachineRepository.findById(id).orElseThrow(() ->
                        new IllegalArgumentExceptionCustom(FarmingMachine.class,
                                IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST)));
    }

    @Override
    @Transactional
    public FarmingMachineDTO addFarmingMachine(FarmingMachineDTO farmingMachineDTO) {
        checkIfRequiredFieldsPresent(farmingMachineDTO);
        checkIfUniqueObject(farmingMachineDTO);
        FarmingMachine farmingMachine = farmingMachineRepository.saveAndFlush(
                rewriteToEntity(farmingMachineDTO, FarmingMachine.NONE));
        return DefaultMappers.farmingMachineMapper.entityToDto(farmingMachine);
    }

    private void checkIfRequiredFieldsPresent(FarmingMachineDTO farmingMachineDTO) {
        if (farmingMachineDTO.getModel() == null || farmingMachineDTO.getProducer() == null ||
                farmingMachineDTO.getModel().isBlank() || farmingMachineDTO.getProducer().isBlank()) {
            throw new IllegalArgumentExceptionCustom(
                    FarmingMachine.class,
                    Set.of("model", "producer"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
    }

    private void checkIfUniqueObject(FarmingMachineDTO farmingMachineDTO) {
        if (farmingMachineDTO.getId() == null && farmingMachineRepository.findByProducerAndModelAndCreatedByIn(
                farmingMachineDTO.getProducer(),
                farmingMachineDTO.getModel(),
                config.allRows()).isEmpty()) {
            return;
        }
        throw new IllegalArgumentExceptionCustom(
                FarmingMachine.class,
                IllegalArgumentExceptionCause.OBJECT_EXISTS);
    }

    private FarmingMachine rewriteToEntity(FarmingMachineDTO dto, FarmingMachine entity) {
        FarmingMachine entityParsed = DefaultMappers.farmingMachineMapper.dtoToEntitySimpleProperties(dto);
        entityParsed.setCreatedBy(config.username());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    @Transactional
    public FarmingMachineDTO updateFarmingMachine(FarmingMachineDTO farmingMachineDTO) {

        FarmingMachine originalMachine = getMachineIfExists(farmingMachineDTO.getId());
        if (originalMachine.equals(getUndefinedFarmingMachine())) {
            throw new IllegalArgumentExceptionCustom(FarmingMachine.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        checkAccess(originalMachine);
        checkIfRequiredFieldsPresent(farmingMachineDTO);

        return DefaultMappers.farmingMachineMapper.entityToDto(farmingMachineRepository.saveAndFlush(
                rewriteToEntity(farmingMachineDTO, originalMachine)));


    }

    private void checkAccess(FarmingMachine machine) {
        if (machine.getCreatedBy().equals(config.username())) {
            return;
        }
        throw new IllegalAccessErrorCustom(FarmingMachine.class,
                IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
    }

    @Override
    public void deleteFarmingMachineSafe(UUID farmingMachineId) {
        FarmingMachine originalMachine = farmingMachineRepository.findById(farmingMachineId)
                .orElse(FarmingMachine.NONE);
        if (originalMachine.equals(FarmingMachine.NONE)) {
            return;
        }
        checkAccess(originalMachine);
        checkUsages(originalMachine);
        farmingMachineRepository.delete(originalMachine);
    }

    private void checkUsages(FarmingMachine originalMachine) {
        if (seedingRepository.findAllByFarmingMachine(originalMachine).isEmpty() &&
                cultivationRepository.findAllByFarmingMachine(originalMachine).isEmpty() &&
                sprayApplicationRepository.findAllByFarmingMachine(originalMachine).isEmpty() &&
                fertilizerApplicationRepository.findAllByFarmingMachine(originalMachine).isEmpty() &&
                harvestRepository.findAllByFarmingMachine(originalMachine).isEmpty()) {
            return;
        }
        throw new IllegalAccessErrorCustom(FarmingMachine.class,
                IllegalAccessErrorCause.USAGE_IN_OTHER_PLACES);
    }

    @Override
    public FarmingMachine getUndefinedFarmingMachine() {
        return farmingMachineRepository.findByProducerAndModelAndCreatedByIn(
                        "UNDEFINED", "UNDEFINED", config.defaultRows())
                .orElse(FarmingMachine.UNDEFINED);
    }

    @Override
    public FarmingMachine getFarmingMachineIfCompatible(UUID farmingMachineId, OperationType operationType) {
        FarmingMachine farmingMachine = getMachineIfExists(farmingMachineId);
        checkCompatibility(operationType, farmingMachine);
        return farmingMachine;
    }

    private void checkCompatibility(OperationType operationType, FarmingMachine farmingMachine) {
        if (farmingMachine.getSupportedOperationTypes().contains(operationType)
                || farmingMachine.getSupportedOperationTypes().contains(OperationType.ANY)) {
            return;
        }
        throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                IllegalArgumentExceptionCause.NOT_COMPATIBLE);
    }

    private FarmingMachine getMachineIfExists(UUID farmingMachineId) {
        if (farmingMachineId == null) {
            return getUndefinedFarmingMachine();
        }
        return farmingMachineRepository.findById(farmingMachineId).orElseThrow(
                () -> new IllegalArgumentExceptionCustom(FarmingMachine.class,
                        IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST)
        );
    }
}