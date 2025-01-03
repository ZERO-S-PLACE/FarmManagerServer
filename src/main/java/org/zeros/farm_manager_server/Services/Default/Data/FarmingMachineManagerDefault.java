package org.zeros.farm_manager_server.Services.Default.Data;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.CustomException.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.CustomException.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
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
    public Page<FarmingMachine> getAllFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getDefaultFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(config.defaultRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getUserFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(config.userRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByNameAs(@NotNull String model, int pageNumber) {
        return farmingMachineRepository.findAllByModelContainingIgnoreCaseAndCreatedByIn(model,
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByProducerAs(@NotNull String producer, int pageNumber) {
        return farmingMachineRepository.findAllByProducerContainingIgnoreCaseAndCreatedByIn(producer,
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByProducerAndNameAs(@NotNull String producer,
                                                                     @NotNull String model, int pageNumber) {
        return farmingMachineRepository
                .findAllByProducerContainingIgnoreCaseAndModelContainingIgnoreCaseAndCreatedByIn(
                        producer, model, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineBySupportedOperation(@NotNull OperationType operationType,
                                                                      int pageNumber) {
        return farmingMachineRepository.findAllBySupportedOperationTypesContainsAndCreatedByIn(
                operationType, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineCriteria(@NotNull String model, @NotNull String producer,
                                                          @NotNull OperationType operationType, int pageNumber) {
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
    public FarmingMachine getFarmingMachineById(@NotNull UUID id) {
        return farmingMachineRepository.findById(id).orElse(FarmingMachine.NONE);
    }

    @Override
    public FarmingMachine addFarmingMachine(@NotNull FarmingMachineDTO farmingMachineDTO) {
        checkIfRequiredFieldsPresent(farmingMachineDTO);
        checkIfUniqueObject(farmingMachineDTO);
        return farmingMachineRepository.saveAndFlush(
                rewriteToEntity(farmingMachineDTO, FarmingMachine.NONE));
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
        if (farmingMachineDTO.getId()==null&&farmingMachineRepository.findByProducerAndModelAndCreatedByIn(
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
        FarmingMachine entityParsed= DefaultMappers.farmingMachineMapper.dtoToEntitySimpleProperties(dto);
        entityParsed.setCreatedBy(config.username());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    public FarmingMachine updateFarmingMachine(@NotNull FarmingMachineDTO farmingMachineDTO) {

        FarmingMachine originalMachine = getMachineIfExists(farmingMachineDTO);
        checkAccess(originalMachine);
        checkIfRequiredFieldsPresent(farmingMachineDTO);

        return farmingMachineRepository.saveAndFlush(
                rewriteToEntity(farmingMachineDTO, originalMachine));


    }

    private FarmingMachine getMachineIfExists( FarmingMachineDTO farmingMachineDTO) {
        if (farmingMachineDTO.getId() == null) {
            throw new IllegalArgumentExceptionCustom(
                    FarmingMachine.class,
                    Set.of("Id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        FarmingMachine originalMachine = getFarmingMachineById(farmingMachineDTO.getId());
        if (originalMachine.equals(FarmingMachine.NONE)) {
            throw new IllegalArgumentExceptionCustom(FarmingMachine.class,
                    IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return originalMachine;

    }

    private void checkAccess(FarmingMachine machine) {
        if (machine.getCreatedBy().equals(config.username())) {
            return;
        }
        throw new IllegalAccessErrorCustom(FarmingMachine.class,
                IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
    }

    @Override
    public void deleteFarmingMachineSafe(@NotNull UUID farmingMachineId) {
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
}