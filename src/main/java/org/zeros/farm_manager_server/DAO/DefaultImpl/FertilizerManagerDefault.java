package org.zeros.farm_manager_server.DAO.DefaultImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.FertilizerManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.model.ApplicationDefaults;
import org.zeros.farm_manager_server.repositories.AgriculturalOperation.FertilizerApplicationRepository;
import org.zeros.farm_manager_server.repositories.AgriculturalOperation.SprayApplicationRepository;
import org.zeros.farm_manager_server.repositories.Data.FertilizerRepository;

import java.util.Set;
import java.util.UUID;
@Component
public class FertilizerManagerDefault implements FertilizerManager {
    private User user;
    private final FertilizerRepository fertilizerRepository;
    private final FertilizerApplicationRepository fertilizerApplicationRepository;
    private final SprayApplicationRepository sprayApplicationRepository;

    public FertilizerManagerDefault(LoggedUserConfiguration loggedUserConfiguration,FertilizerRepository fertilizerRepository, FertilizerApplicationRepository fertilizerApplicationRepository, SprayApplicationRepository sprayApplicationRepository) {
        this.fertilizerRepository = fertilizerRepository;
        this.fertilizerApplicationRepository = fertilizerApplicationRepository;
        this.sprayApplicationRepository = sprayApplicationRepository;
        this.user=loggedUserConfiguration.getLoggedUserProperty().get();
        loggedUserConfiguration.getLoggedUserProperty().addListener(((observable, oldValue, newValue) -> user=newValue));
    }
    @Override
    public Page<Fertilizer> getAllFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(Set.of("ADMIN", user.getUsername()), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }

    @Override
    public Page<Fertilizer> getDefaultFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(Set.of("ADMIN"), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }

    @Override
    public Page<Fertilizer> getUserFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(Set.of( user.getUsername()), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }

    @Override
    public Page<Fertilizer> getFertilizerByNameAs(String name,int pageNumber) {
        return fertilizerRepository.findAllByNameContainingAndCreatedByIn(name,Set.of("ADMIN", user.getUsername()), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));

    }
    @Override
    public Page<Fertilizer> getNaturalFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByIsNaturalFertilizerAndCreatedByIn(
                true,Set.of("ADMIN", user.getUsername()),
                PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));

    }
    @Override
    public Page<Fertilizer> getSyntheticFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByIsNaturalFertilizerAndCreatedByIn(
                false,Set.of("ADMIN", user.getUsername()),
                PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }

    @Override
    public Fertilizer getFertilizerById(UUID id) {
        return fertilizerRepository.findById(id).orElse(Fertilizer.NONE);
    }

    @Override
    public Fertilizer addFertilizer(Fertilizer fertilizer) {
        if(fertilizer.getName().isBlank()){
            return Fertilizer.NONE;
        }
        if(fertilizerRepository.findByNameAndProducerAndCreatedByIn(
                fertilizer.getName(),fertilizer.getProducer(),
                Set.of("ADMIN", user.getUsername()))
                .isEmpty()) {
            fertilizer.setCreatedBy(user.getUsername());
            return fertilizerRepository.saveAndFlush(fertilizer);
        }
        return Fertilizer.NONE;
    }

    @Override
    public Fertilizer updateFertilizer(Fertilizer fertilizer) {

        Fertilizer originalFertilizer=fertilizerRepository.findById(fertilizer.getId()).orElse(Fertilizer.NONE);
        if(originalFertilizer.equals(Fertilizer.NONE)){
            return Fertilizer.NONE;
        }
        if(originalFertilizer.getCreatedBy().equals(user.getUsername())){
            if(fertilizer.getName().isBlank())
            {
                return Fertilizer.NONE;
            }
            return fertilizerRepository.saveAndFlush(fertilizer);
        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

    @Override
    public void deleteFertilizerSafe(Fertilizer fertilizer) {
        Fertilizer originalFertilizer=fertilizerRepository.findById(fertilizer.getId()).orElse(Fertilizer.NONE);
        if (originalFertilizer.getCreatedBy().equals(user.getUsername())) {
            if(fertilizerApplicationRepository.findAllByFertilizer(fertilizer).isEmpty()) {
                if(sprayApplicationRepository.findAllByFertilizer(fertilizer).isEmpty()) {
                    fertilizerRepository.delete(fertilizer);
                    return;
                }
            }
            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }
}
