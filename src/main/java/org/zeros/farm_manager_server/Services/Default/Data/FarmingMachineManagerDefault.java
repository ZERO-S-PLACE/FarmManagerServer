package org.zeros.farm_manager_server.Services.Default.Data;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.*;
import org.zeros.farm_manager_server.Repositories.Data.FarmingMachineRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;

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

    @Override
    public Page<FarmingMachine> getAllFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(config.allRows(), getPageRequest(pageNumber));
    }

    private static PageRequest getPageRequest(int pageNumber) {
        if(pageNumber<0) pageNumber = 0;
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model"));
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
    public Page<FarmingMachine> getFarmingMachineByNameAs(String model, int pageNumber) {
        return farmingMachineRepository.findAllByModelContainingIgnoreCaseAndCreatedByIn(model, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByProducerAs(String producer, int pageNumber) {
        return farmingMachineRepository.findAllByProducerContainingIgnoreCaseAndCreatedByIn(producer, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByProducerAndNameAs(String producer,
                                                                     String model, int pageNumber) {
        return farmingMachineRepository
                .findAllByProducerContainingIgnoreCaseAndModelContainingIgnoreCaseAndCreatedByIn(
                        producer, model, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineBySupportedOperation(OperationType operationType,
                                                                      int pageNumber) {
        return farmingMachineRepository.findAllBySupportedOperationTypesContainsAndCreatedByIn(
                operationType, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineCriteria(String model, String producer,
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
    public FarmingMachine getFarmingMachineById(UUID id) {
        return farmingMachineRepository.findById(id).orElse(FarmingMachine.NONE);
    }

    @Override
    public FarmingMachine addFarmingMachine(FarmingMachine farmingMachine) {
        if (farmingMachine.getModel().isBlank() || farmingMachine.getProducer().isBlank()) {
            throw new IllegalArgumentException("FarmingMachine model and producer are required");
        }
        if (farmingMachineRepository.findByProducerAndModelAndCreatedByIn(
                farmingMachine.getProducer(), farmingMachine.getModel(), config.allRows()).isEmpty()) {
            farmingMachine.setCreatedBy(config.username());
            return farmingMachineRepository.saveAndFlush(farmingMachine);
        }
        throw new IllegalArgumentException("Farming machine already exists");
    }

    @Override
    public FarmingMachine updateFarmingMachine(FarmingMachine farmingMachine) {
        FarmingMachine originalMachine = farmingMachineRepository.findById(farmingMachine.getId())
                .orElse(FarmingMachine.NONE);
        if (originalMachine.equals(FarmingMachine.NONE)) {
            throw new IllegalArgumentException("FarmingMachine not found");
        }
        if (originalMachine.getCreatedBy().equals(config.username())) {
            if (farmingMachine.getModel().isBlank() || farmingMachine.getProducer().isBlank()) {
                throw new IllegalArgumentException("FarmingMachine model and producer are required");
            }
            originalMachine.setModel(farmingMachine.getModel());
            originalMachine.setProducer(farmingMachine.getProducer());
            originalMachine.setDescription(farmingMachine.getDescription());
            originalMachine.setSupportedOperationTypes(farmingMachine.getSupportedOperationTypes());
            return farmingMachineRepository.saveAndFlush(originalMachine);

        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

    @Override
    public void deleteFarmingMachineSafe(FarmingMachine farmingMachine) {
        FarmingMachine originalMachine = farmingMachineRepository.findById(farmingMachine.getId())
                .orElse(FarmingMachine.NONE);
        if (originalMachine.getCreatedBy().equals(config.username())) {
            if (seedingRepository.findAllByFarmingMachine(farmingMachine).isEmpty() &&
                    cultivationRepository.findAllByFarmingMachine(farmingMachine).isEmpty() &&
                    sprayApplicationRepository.findAllByFarmingMachine(farmingMachine).isEmpty() &&
                    fertilizerApplicationRepository.findAllByFarmingMachine(farmingMachine).isEmpty() &&
                    harvestRepository.findAllByFarmingMachine(farmingMachine).isEmpty()) {
                farmingMachineRepository.delete(farmingMachine);
                return;
            }

            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }

    @Override
    public FarmingMachine getUndefinedFarmingMachine() {
        return farmingMachineRepository.findByProducerAndModelAndCreatedByIn(
                "UNDEFINED", "UNDEFINED", config.defaultRows())
                .orElse(FarmingMachine.UNDEFINED);
    }
}
