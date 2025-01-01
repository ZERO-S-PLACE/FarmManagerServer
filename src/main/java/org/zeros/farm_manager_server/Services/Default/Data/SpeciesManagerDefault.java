package org.zeros.farm_manager_server.Services.Default.Data;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.Repositories.Data.SpeciesRepository;
import org.zeros.farm_manager_server.Repositories.Data.SubsideRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class SpeciesManagerDefault implements SpeciesManager {

    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;
    private final SubsideRepository subsideRepository;
    private final LoggedUserConfiguration config;


    private static void checkSpeciesConstraints(Species species) {
        if (species.getName().isBlank() || species.getFamily().isBlank()) {
            throw new IllegalArgumentException("Species name and family are required");
        }
    }

    private static PageRequest getPageRequest(int pageNumber) {
        if (pageNumber < 0) pageNumber = 0;
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name"));
    }

    @Override
    public Page<Species> getAllSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Species> getDefaultSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(config.defaultRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Species> getUserSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(config.userRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Species> getSpeciesByNameAs(String name, int pageNumber) {
        return speciesRepository.findAllByNameContainsIgnoreCaseAndCreatedByIn(name, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Species> getSpeciesByFamilyAs(String family, int pageNumber) {
        return speciesRepository.findAllByFamilyContainsIgnoreCaseAndCreatedByIn(family, config.allRows(), getPageRequest(pageNumber));
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
