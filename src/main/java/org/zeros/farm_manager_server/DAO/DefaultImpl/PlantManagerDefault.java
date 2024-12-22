package org.zeros.farm_manager_server.DAO.DefaultImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.PlantManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.model.ApplicationDefaults;
import org.zeros.farm_manager_server.repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.repositories.Data.PlantRepository;
import java.util.UUID;

@Component
public class PlantManagerDefault implements PlantManager {

    private final LoggedUserConfiguration config;
    private final PlantRepository plantRepository;
    private final CropRepository cropRepository;

    public PlantManagerDefault(LoggedUserConfiguration loggedUserConfiguration, PlantRepository plantRepository, CropRepository cropRepository) {
        this.plantRepository = plantRepository;
        this.cropRepository = cropRepository;
        this.config = loggedUserConfiguration;
    }


    @Override
    public Page<Plant> getAllPlants(int pageNumber) {
        return plantRepository.findAllByCreatedByIn(config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("variety")));
    }

    @Override
    public Page<Plant> getDefaultPlants(int pageNumber) {
        return plantRepository.findAllByCreatedByIn(config.defaultRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("variety")));
    }

    @Override
    public Page<Plant> getUserPlants(int pageNumber) {
        return plantRepository.findAllByCreatedByIn(config.userRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("variety")));
    }

    @Override
    public Page<Plant> getPlantsByVarietyAs(String variety, int pageNumber) {
        return plantRepository.findAllByVarietyContainingIgnoreCaseAndCreatedByIn(variety, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("variety")));
    }

    @Override
    public Page<Plant> getPlantsBySpecies(Species species, int pageNumber) {
        return plantRepository.findAllBySpeciesAndCreatedByIn(species, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("variety")));
    }

    @Override
    public Page<Plant> getPlantsByVarietyAndSpecies(String variety, Species species, int pageNumber) {
        return plantRepository.findAllBySpeciesAndVarietyContainingIgnoreCaseAndCreatedByIn(species, variety, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("variety")));
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

    private static void checkPlantConstraints(Plant plant) {
        if (plant.getVariety().isBlank() || plant.getSpecies().equals(Species.NONE)) {
            throw new IllegalArgumentException("Variety and species must be specified");
        }
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
}
