package org.zeros.farm_manager_server.Services.Default.Data;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.Repositories.Data.SpeciesRepository;
import org.zeros.farm_manager_server.Repositories.Data.SubsideRepository;

import java.util.UUID;

@Service
@Primary
public class SpeciesManagerDefault implements SpeciesManager {

    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;
    private final SubsideRepository subsideRepository;
    private final LoggedUserConfiguration config;


    public SpeciesManagerDefault(LoggedUserConfiguration loggedUserConfiguration, PlantRepository plantRepository, SpeciesRepository speciesRepository, SubsideRepository subsideRepository, LoggedUserConfiguration config) {
        this.plantRepository = plantRepository;
        this.speciesRepository = speciesRepository;
        this.subsideRepository = subsideRepository;
        this.config = loggedUserConfiguration;
    }

    private static void checkSpeciesConstraints(Species species) {
        if (species.getName().isBlank() || species.getFamily().isBlank()) {
            throw new IllegalArgumentException("Species name and family are required");
        }
    }

    @Override
    public Page<Species> getAllSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Species> getDefaultSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(config.defaultRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Species> getUserSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(config.userRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Species> getSpeciesByNameAs(String name, int pageNumber) {
        return speciesRepository.findAllByNameContainsIgnoreCaseAndCreatedByIn(name, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Species> getSpeciesByFamilyAs(String family, int pageNumber) {
        return speciesRepository.findAllByFamilyContainsIgnoreCaseAndCreatedByIn(family, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Species getSpeciesById(UUID id) {
        return speciesRepository.findById(id).orElse(Species.NONE);
    }

    @Override
    public Species addSpecies(Species species) {
        checkSpeciesConstraints(species);
        if (speciesRepository.findByNameAndFamilyAndCreatedByIn(species.getName(), species.getFamily(), config.allRows()).isPresent()) {
            throw new IllegalArgumentException("This species already exists");
        }
        species.setCreatedBy(config.username());
        return speciesRepository.saveAndFlush(species);
    }

    @Override
    public Species updateSpecies(Species species) {
        Species originalSpecies = speciesRepository.findById(species.getId()).orElse(Species.NONE);
        if (originalSpecies.equals(Species.NONE)) {
            throw new IllegalArgumentException("Species not found");
        }
        if (originalSpecies.getCreatedBy().equals(config.username())) {
            checkSpeciesConstraints(species);
            return speciesRepository.saveAndFlush(species);
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }

    @Override
    public void deleteSpeciesSafe(Species species) {
        Species originalSpecies = speciesRepository.findById(species.getId()).orElse(Species.NONE);
        if (originalSpecies.getCreatedBy().equals(config.username())) {
            if (plantRepository.findAllBySpeciesAndCreatedByIn(species, config.allRows(), PageRequest.of(0, 1)).isEmpty()) {
                if (subsideRepository.findAllBySpeciesAllowedContains(species).isEmpty()) {
                    speciesRepository.delete(species);
                    return;
                }
            }
            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

}
