package org.zeros.farm_manager_server.DAO.DefaultImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.SpeciesManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.model.ApplicationDefaults;
import org.zeros.farm_manager_server.repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.repositories.Data.SpeciesRepository;
import org.zeros.farm_manager_server.repositories.Data.SubsideRepository;

import java.rmi.NoSuchObjectException;
import java.util.Set;
import java.util.UUID;
@Component
public class SpeciesManagerDefault implements SpeciesManager {

    private User user;
    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;
    private final SubsideRepository subsideRepository;

    public SpeciesManagerDefault(LoggedUserConfiguration loggedUserConfiguration, PlantRepository plantRepository, SpeciesRepository speciesRepository, SubsideRepository subsideRepository) {
        this.plantRepository = plantRepository;
        this.speciesRepository = speciesRepository;
        this.subsideRepository = subsideRepository;
        this.user=loggedUserConfiguration.getLoggedUserProperty().get();
        loggedUserConfiguration.getLoggedUserProperty().addListener(((observable, oldValue, newValue) -> user=newValue));
    }

    @Override
    public Page<Species> getAllSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Species> getDefaultSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(Set.of("ADMIN"),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Species> getUserSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(Set.of(user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Species> getSpeciesByNameAs(String name, int pageNumber) {
        return speciesRepository.findAllByNameContainsIgnoreCaseAndCreatedByIn(name,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Species> getSpeciesByFamilyAs(String family, int pageNumber) {
        return speciesRepository.findAllByFamilyContainsIgnoreCaseAndCreatedByIn(family,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Species getSpeciesById(UUID id) {
        return speciesRepository.findById(id).orElse(Species.NONE);
    }

    @Override
    public Species addSpecies(Species species) {
        if(speciesRepository.findByNameAndFamilyAndCreatedByIn(species.getName(),species.getFamily(),Set.of("ADMIN", user.getUsername())).isPresent())
        {
            return Species.NONE;
        }
        species.setCreatedBy(user.getUsername());
        return speciesRepository.saveAndFlush(species);
    }

    @Override
    public Species updateSpecies(Species species) throws NoSuchObjectException {
        Species originalSpecies=speciesRepository.findById(species.getId()).orElse(Species.NONE);
        if(originalSpecies.equals(Species.NONE)){
            return Species.NONE;
        }
        if(originalSpecies.getCreatedBy().equals(user.getUsername())){
            if(species.getName().isBlank()||species.getFamily().isBlank()){
                return Species.NONE;
            }
            return speciesRepository.saveAndFlush(species);
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }

    @Override
    public void deleteSpeciesSafe(Species species) {
        Species originalSpecies = speciesRepository.findById(species.getId()).orElse(Species.NONE);
        if (originalSpecies.getCreatedBy().equals(user.getUsername())) {
            if(plantRepository.findAllBySpeciesAndCreatedByIn(species,Set.of("ADMIN", user.getUsername()),PageRequest.of(0,1)).isEmpty()){
                if(subsideRepository.findAllBySpeciesAllowedContains(species).isEmpty()){
                    speciesRepository.delete(species);
                    return;
                }
            }
            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

}
