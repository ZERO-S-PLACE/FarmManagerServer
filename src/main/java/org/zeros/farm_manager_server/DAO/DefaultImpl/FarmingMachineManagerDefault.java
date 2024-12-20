package org.zeros.farm_manager_server.DAO.DefaultImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.FarmingMachineManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.model.ApplicationDefaults;
import org.zeros.farm_manager_server.repositories.AgriculturalOperation.*;
import org.zeros.farm_manager_server.repositories.Data.FarmingMachineRepository;

import java.util.Set;
import java.util.UUID;
@Component
public class FarmingMachineManagerDefault implements FarmingMachineManager {
    private User user;
    private final FarmingMachineRepository farmingMachineRepository;
    private final SeedingRepository seedingRepository;
    private final CultivationRepository cultivationRepository;
    private final SprayApplicationRepository sprayApplicationRepository;
    private final FertilizerApplicationRepository fertilizerApplicationRepository;
    private final HarvestRepository harvestRepository;

    public FarmingMachineManagerDefault(LoggedUserConfiguration loggedUserConfiguration,FarmingMachineRepository farmingMachineRepository, SeedingRepository seedingRepository, CultivationRepository cultivationRepository, SprayApplicationRepository sprayApplicationRepository, FertilizerApplicationRepository fertilizerApplicationRepository, HarvestRepository harvestRepository) {
        this.farmingMachineRepository = farmingMachineRepository;
        this.seedingRepository = seedingRepository;
        this.cultivationRepository = cultivationRepository;
        this.sprayApplicationRepository = sprayApplicationRepository;
        this.fertilizerApplicationRepository = fertilizerApplicationRepository;
        this.harvestRepository = harvestRepository;
        this.user=loggedUserConfiguration.getLoggedUserProperty().get();
        loggedUserConfiguration.getLoggedUserProperty().addListener(((observable, oldValue, newValue) -> user=newValue));
    }

    @Override
    public Page<FarmingMachine> getAllFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getDefaultFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(Set.of("ADMIN"),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getUserFarmingMachines(int pageNumber) {
        return farmingMachineRepository.findAllByCreatedByIn(Set.of(user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByNameAs(String model, int pageNumber) {
        return farmingMachineRepository.findAllByModelContainingIgnoreCaseAndCreatedByIn(
                model,
                Set.of("ADMIN", user.getUsername()),
                PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByProducerAs(String producer, int pageNumber) {
        return farmingMachineRepository.findAllByProducerContainingIgnoreCaseAndCreatedByIn(
                producer,
                Set.of("ADMIN", user.getUsername()),
                PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));
    }

    @Override
    public Page<FarmingMachine> getFarmingMachineByProducerAndNameAs(String producer,String model, int pageNumber) {
        return farmingMachineRepository.findAllByProducerContainingIgnoreCaseAndModelContainingIgnoreCaseAndCreatedByIn(
                producer,
                model,
                Set.of("ADMIN", user.getUsername()),
                PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("model")));    }

    @Override
    public FarmingMachine getFarmingMachineById(UUID id) {
        return farmingMachineRepository.findById(id).orElse(FarmingMachine.NONE);
    }

    @Override
    public FarmingMachine addFarmingMachine(FarmingMachine farmingMachine) {
        if(farmingMachine.getModel().isBlank()||farmingMachine.getProducer().isBlank()) {
            return FarmingMachine.NONE;
        }
            if (farmingMachineRepository.findByProducerAndModelAndCreatedByIn(
                    farmingMachine.getProducer(),
                    farmingMachine.getModel(),
                    Set.of("ADMIN", user.getUsername())
            ).isEmpty()){
                farmingMachine.setCreatedBy(user.getUsername());
                return farmingMachineRepository.saveAndFlush(farmingMachine);
            }

        return FarmingMachine.NONE;
    }

    @Override
    public FarmingMachine updateFarmingMachine(FarmingMachine farmingMachine) {
        FarmingMachine originalMachine=farmingMachineRepository.findById(farmingMachine.getId()).orElse(FarmingMachine.NONE);
        if(originalMachine.equals(FarmingMachine.NONE)){
            return FarmingMachine.NONE;
        }
        if(originalMachine.getCreatedBy().equals(user.getUsername())){
            if(farmingMachine.getModel().isBlank()||farmingMachine.getProducer().isBlank()) {
                return FarmingMachine.NONE;
            }
            return farmingMachineRepository.saveAndFlush(farmingMachine);

        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

    @Override
    public void deleteFarmingMachineSafe(FarmingMachine farmingMachine) {
        FarmingMachine originalMachine=farmingMachineRepository.findById(farmingMachine.getId()).orElse(FarmingMachine.NONE);
        if (originalMachine.getCreatedBy().equals(user.getUsername())) {
            if(seedingRepository.findAllByFarmingMachine(farmingMachine).isEmpty()&&
                    cultivationRepository.findAllByFarmingMachine(farmingMachine).isEmpty()&&
                    sprayApplicationRepository.findAllByFarmingMachine(farmingMachine).isEmpty()&&
                    fertilizerApplicationRepository.findAllByFarmingMachine(farmingMachine).isEmpty()&&
                    harvestRepository.findAllByFarmingMachine(farmingMachine).isEmpty())
            {
                farmingMachineRepository.delete(farmingMachine);
                return;
            }

            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }
}
