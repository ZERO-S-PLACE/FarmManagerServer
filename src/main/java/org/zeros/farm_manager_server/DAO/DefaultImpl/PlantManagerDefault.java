package org.zeros.farm_manager_server.DAO.DefaultImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.PlantManager;
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
public class PlantManagerDefault implements PlantManager {

    private User user;
    private final PlantRepository plantRepository;
    private final CropRepository cropRepository;

    public PlantManagerDefault(LoggedUserConfiguration loggedUserConfiguration, PlantRepository plantRepository, SpeciesRepository speciesRepository, SubsideRepository subsideRepository, CropRepository cropRepository) {
        this.plantRepository = plantRepository;
        this.cropRepository = cropRepository;
        this.user=loggedUserConfiguration.getLoggedUserProperty().get();
        loggedUserConfiguration.getLoggedUserProperty().addListener(((observable, oldValue, newValue) -> user=newValue));
    }


    @Override
    public Page<Plant> getAllPlants(int pageNumber) {
        return plantRepository.findAllByCreatedByIn(Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("variety")));
    }

    @Override
    public Page<Plant> getDefaultPlants(int pageNumber) {
        return plantRepository.findAllByCreatedByIn(Set.of("ADMIN"),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("variety")));
    }

    @Override
    public Page<Plant> getUserPlants(int pageNumber) {
        return plantRepository.findAllByCreatedByIn(Set.of(user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("variety")));
    }

    @Override
    public Page<Plant> getPlantsByVarietyAs(String variety, int pageNumber) {
        return plantRepository.findAllByVarietyContainingIgnoreCaseAndCreatedByIn(variety,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("variety")));
    }

    @Override
    public Page<Plant> getPlantsBySpecies(Species species, int pageNumber) {
        return plantRepository.findAllBySpeciesAndCreatedByIn(species,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber,ApplicationDefaults.pageSize,
                Sort.by("variety")));
    }

    @Override
    public Page<Plant> getPlantsByVarietyAndSpecies(String variety, Species species, int pageNumber) {
        return plantRepository.findAllBySpeciesAndVarietyContainingIgnoreCaseAndCreatedByIn(species,variety,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber,ApplicationDefaults.pageSize,
                Sort.by("variety")));
    }

    @Override
    public Plant getPlantById(UUID uuid) {
        return plantRepository.findById(uuid).orElse(Plant.NONE);
    }

    @Override
    public Plant addPlant(Plant plant) {
        if((plant.getVariety().isBlank())||plant.getSpecies().equals(Species.NONE)) {
            return Plant.NONE;
        }
            if (plantRepository.findAllBySpeciesAndVarietyAndCreatedByIn(
                    plant.getSpecies(), plant.getVariety(),Set.of("ADMIN", user.getUsername()), PageRequest.of(0, 1)).isEmpty()) {
                plant.setCreatedBy(user.getUsername());
                return plantRepository.saveAndFlush(plant);
            }

        return Plant.NONE;
    }

    @Override
    public Plant updatePlant(Plant plant)  {
        Plant originalPlant=plantRepository.findById(plant.getId()).orElse(Plant.NONE);
        if(originalPlant.equals(Plant.NONE)){
           return Plant.NONE;
        }
        if(originalPlant.getCreatedBy().equals(user.getUsername())){
            if(plant.getVariety().isBlank()||plant.getSpecies().equals(Species.NONE)){
                return Plant.NONE;
            }
            return plantRepository.saveAndFlush(plant);
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }

    @Override
    public void deletePlantSafe(Plant plant) {

        Plant originalPlant = plantRepository.findById(plant.getId()).orElse(Plant.NONE);
        if (originalPlant.getCreatedBy().equals(user.getUsername())) {
            if(cropRepository.findAllByCultivatedPlantsContains(plant).isEmpty()){
                plantRepository.delete(plant);
                return;
            }

            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }
}
