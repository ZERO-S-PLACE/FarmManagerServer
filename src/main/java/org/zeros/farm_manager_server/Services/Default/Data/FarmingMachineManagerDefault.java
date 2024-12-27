package org.zeros.farm_manager_server.Services.Default.Data;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.*;
import org.zeros.farm_manager_server.Repositories.Data.FarmingMachineRepository;

import java.util.UUID;

@Service
@Primary
public class FarmingMachineManagerDefault implements FarmingMachineManager {
    private final LoggedUserConfiguration config;
    private final FarmingMachineRepository farmingMachineRepository;
    private final SeedingRepository seedingRepository;
    private final CultivationRepository cultivationRepository;
    private final SprayApplicationRepository sprayApplicationRepository;
    private final FertilizerApplicationRepository fertilizerApplicationRepository;
    private final HarvestRepository harvestRepository;

    public FarmingMachineManagerDefault(LoggedUserConfiguration loggedUserConfiguration, FarmingMachineRepository farmingMachineRepository, SeedingRepository seedingRepository, CultivationRepository cultivationRepository, SprayApplicationRepository sprayApplicationRepository, FertilizerApplicationRepository fertilizerApplicationRepository, HarvestRepository harvestRepository) {
        this.farmingMachineRepository = farmingMachineRepository;
        this.seedingRepository = seedingRepository;
        this.cultivationRepository = cultivationRepository;
        this.sprayApplicationRepository = sprayApplicationRepository;
        this.fertilizerApplicationRepository = fertilizerApplicationRepository;
        this.harvestRepository = harvestRepository;
        this.config = loggedUserConfiguration;

    }

    @Override
    public Page<FarmingMachine> getAllFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getDefaultFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(config.defaultRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getUserFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(config.userRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByNameAs(String model, int pageNumber) {
        return farmingMachineRepository.findAllByModelContainingIgnoreCaseAndCreatedByIn(model, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByProducerAs(String producer, int pageNumber) {
        return farmingMachineRepository.findAllByProducerContainingIgnoreCaseAndCreatedByIn(producer, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByProducerAndNameAs(String producer, String model, int pageNumber) {
        return farmingMachineRepository.findAllByProducerContainingIgnoreCaseAndModelContainingIgnoreCaseAndCreatedByIn(producer, model, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineBySupportedOperation(OperationType operationType, int pageNumber) {
        return farmingMachineRepository.findAllBySupportedOperationTypesContainsAndCreatedByIn(operationType, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
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
        if (farmingMachineRepository.findByProducerAndModelAndCreatedByIn(farmingMachine.getProducer(), farmingMachine.getModel(), config.allRows()).isEmpty()) {
            farmingMachine.setCreatedBy(config.username());
            return farmingMachineRepository.saveAndFlush(farmingMachine);
        }
        throw new IllegalArgumentException("Farming machine already exists");
    }

    @Override
    public FarmingMachine updateFarmingMachine(FarmingMachine farmingMachine) {
        FarmingMachine originalMachine = farmingMachineRepository.findById(farmingMachine.getId()).orElse(FarmingMachine.NONE);
        if (originalMachine.equals(FarmingMachine.NONE)) {
            throw new IllegalArgumentException("FarmingMachine not found");
        }
        if (originalMachine.getCreatedBy().equals(config.username())) {
            if (farmingMachine.getModel().isBlank() || farmingMachine.getProducer().isBlank()) {
                throw new IllegalArgumentException("FarmingMachine model and producer are required");
            }
            return farmingMachineRepository.saveAndFlush(farmingMachine);

        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

    @Override
    public void deleteFarmingMachineSafe(FarmingMachine farmingMachine) {
        FarmingMachine originalMachine = farmingMachineRepository.findById(farmingMachine.getId()).orElse(FarmingMachine.NONE);
        if (originalMachine.getCreatedBy().equals(config.username())) {
            if (seedingRepository.findAllByFarmingMachine(farmingMachine).isEmpty() && cultivationRepository.findAllByFarmingMachine(farmingMachine).isEmpty() && sprayApplicationRepository.findAllByFarmingMachine(farmingMachine).isEmpty() && fertilizerApplicationRepository.findAllByFarmingMachine(farmingMachine).isEmpty() && harvestRepository.findAllByFarmingMachine(farmingMachine).isEmpty()) {
                farmingMachineRepository.delete(farmingMachine);
                return;
            }

            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }

    @Override
    public FarmingMachine getUndefinedFarmingMachine() {
        return farmingMachineRepository.findByProducerAndModelAndCreatedByIn("UNDEFINED", "UNDEFINED", config.defaultRows()).orElse(FarmingMachine.UNDEFINED);
    }
}
