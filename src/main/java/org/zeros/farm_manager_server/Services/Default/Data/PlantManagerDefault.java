package org.zeros.farm_manager_server.Services.Default.Data;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.PlantManager;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class PlantManagerDefault implements PlantManager {

    private final LoggedUserConfiguration config;
    private final PlantRepository plantRepository;
    private final CropRepository cropRepository;
    private final SpeciesManager speciesManager;


    private static void checkPlantConstraints(Plant plant) {
        if (plant.getVariety().isBlank() || plant.getSpecies().equals(Species.NONE)) {
            throw new IllegalArgumentException("Variety and species must be specified");
        }
    }

    private static PageRequest getPageRequest(int pageNumber) {
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("variety"));
    }

    @Override
    public Page<Plant> getAllPlants(int pageNumber) {
        if (pageNumber < 0) pageNumber = 0;
        return plantRepository.findAllByCreatedByIn(config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getDefaultPlants(int pageNumber) {
        return plantRepository.findAllByCreatedByIn(config.defaultRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getUserPlants(int pageNumber) {
        return plantRepository.findAllByCreatedByIn(config.userRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getPlantsByVarietyAs(String variety, int pageNumber) {
        return plantRepository.findAllByVarietyContainingIgnoreCaseAndCreatedByIn(variety, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getPlantsBySpecies(Species species, int pageNumber) {
        return plantRepository.findAllBySpeciesAndCreatedByIn(species, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getPlantsByVarietyAndSpecies(String variety, Species species, int pageNumber) {
        return plantRepository.findAllBySpeciesAndVarietyContainingIgnoreCaseAndCreatedByIn(species, variety, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Plant getPlantById(UUID uuid) {
        return plantRepository.findById(uuid).orElse(Plant.NONE);
    }

    @Override
    public Plant addPlant(Plant plant) {
        checkPlantConstraints(plant);
        if (plantRepository.findAllBySpeciesAndVarietyAndCreatedByIn(plant.getSpecies(), plant.getVariety(), config.allRows(), PageRequest.of(0, 1)).isEmpty()) {
            plant.setCreatedBy(config.username());
            return plantRepository.saveAndFlush(plant);
        }
        throw new IllegalArgumentException("Plant already exists");
    }

    @Override
    public Plant updatePlant(Plant plant) {
        Plant originalPlant = plantRepository.findById(plant.getId()).orElse(Plant.NONE);
        if (originalPlant.equals(Plant.NONE)) {
            throw new IllegalArgumentException("Plant not found");
        }
        if (originalPlant.getCreatedBy().equals(config.username())) {
            checkPlantConstraints(plant);
            return plantRepository.saveAndFlush(plant);
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }

    @Override
    public void deletePlantSafe(Plant plant) {

        Plant originalPlant = plantRepository.findById(plant.getId()).orElse(Plant.NONE);
        if (originalPlant.getCreatedBy().equals(config.username())) {
            if (cropRepository.findAllByCultivatedPlantsContains(plant).isEmpty()) {
                plantRepository.delete(plant);
                return;
            }

            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }

    @Override
    public Page<Plant> getPlantsCriteria(String variety, UUID speciesId, Integer pageNumber) {
        boolean varietyNotPresent = variety == null || variety.isEmpty();
        Species species = speciesManager.getSpeciesById(speciesId);
        if (species == Species.NONE) {
            if (varietyNotPresent) {
                return getAllPlants(pageNumber);
            }
            return getPlantsByVarietyAs(variety, pageNumber);
        }
        if (varietyNotPresent) {
            return getPlantsBySpecies(species, pageNumber);
        }
        return getPlantsByVarietyAndSpecies(variety, species, pageNumber);
    }
}
