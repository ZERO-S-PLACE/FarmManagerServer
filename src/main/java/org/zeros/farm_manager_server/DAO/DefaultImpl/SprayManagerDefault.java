package org.zeros.farm_manager_server.DAO.DefaultImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.SprayManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.SprayType;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.Crops.Subside;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.model.ApplicationDefaults;
import org.zeros.farm_manager_server.repositories.AgriculturalOperation.SprayApplicationRepository;
import org.zeros.farm_manager_server.repositories.Data.SprayRepository;

import java.rmi.NoSuchObjectException;
import java.util.Set;
import java.util.UUID;
@Component
public class SprayManagerDefault implements SprayManager {

    private User user;
    private final SprayRepository sprayRepository;
    private final SprayApplicationRepository sprayApplicationRepository;

    public SprayManagerDefault(LoggedUserConfiguration loggedUserConfiguration, @Autowired SprayRepository sprayRepository, SprayApplicationRepository sprayApplicationRepository) {
        this.sprayRepository = sprayRepository;
        this.sprayApplicationRepository = sprayApplicationRepository;
        this.user=loggedUserConfiguration.getLoggedUserProperty().get();
        loggedUserConfiguration.getLoggedUserProperty().addListener(((observable, oldValue, newValue) -> user=newValue));
    }

    @Override
    public Page<Spray> getAllSprays(int pageNumber) {
        return sprayRepository.findAllByCreatedByIn(Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name"))) ;
    }

    @Override
    public Page<Spray> getDefaultSprays(int pageNumber) {
        return sprayRepository.findAllByCreatedByIn(Set.of("ADMIN"),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name"))) ;
    }

    @Override
    public Page<Spray> getUserSprays(int pageNumber) {
        return sprayRepository.findAllByCreatedByIn(Set.of(user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name"))) ;
    }

    @Override
    public Page<Spray> getSpraysByNameAs(String name, int pageNumber) {
        return sprayRepository.findAllByNameContainingIgnoreCaseAndCreatedByIn(name,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Spray> getSpraysByProducerAs(String producer, int pageNumber) {
        return sprayRepository.findAllByProducerContainingIgnoreCaseAndCreatedByIn(producer,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Spray> getSpraysBySprayType(SprayType sprayType, int pageNumber) {
        return sprayRepository.findAllBySprayTypeAndCreatedByIn(sprayType,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Spray> getSpraysByActiveSubstance(String activeSubstance, int pageNumber) {
        return sprayRepository.findAllByActiveSubstancesContainsAndCreatedByIn(activeSubstance,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Spray getSprayById(UUID uuid) {
        return sprayRepository.findById(uuid).orElse(Spray.NONE);
    }

    @Override
    public Spray addSpray(Spray spray) {
        if(spray.getName().isBlank()){
            return Spray.NONE;
        }
        if(sprayRepository.findByNameAndProducerAndCreatedByIn(spray.getName(),spray.getProducer(),Set.of("ADMIN", user.getUsername())).isPresent()){
            return Spray.NONE;
        }
        spray.setCreatedBy(user.getUsername());
        return sprayRepository.saveAndFlush(spray);
    }

    @Override
    public Spray updateSpray(Spray spray) {
        Spray originalSpray=sprayRepository.findById(spray.getId()).orElse(Spray.NONE);
        if(originalSpray.equals(Spray.NONE)){
            return Spray.NONE;
        }
        if(originalSpray.getCreatedBy().equals(user.getUsername())){
            if(spray.getName().isBlank()){
               return Spray.NONE;
            }
            return sprayRepository.saveAndFlush(spray);
        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

    @Override
    public void deleteSpraySafe(Spray spray) {

        Spray originalSpray = sprayRepository.findById(spray.getId()).orElse(Spray.NONE);
        if (originalSpray.getCreatedBy().equals(user.getUsername())) {
                if(sprayApplicationRepository.findAllBySpray(spray).isEmpty())
                {
                    sprayRepository.delete(spray);
                    return ;
                }

            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }
}
