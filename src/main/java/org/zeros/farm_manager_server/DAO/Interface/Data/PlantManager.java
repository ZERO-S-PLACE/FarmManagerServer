package org.zeros.farm_manager_server.DAO.Interface.Data;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crop.Plant.Species;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

public interface PlantManager {

    Page<Plant> getAllPlants(int pageNumber);

    Page<Plant> getDefaultPlants(int pageNumber);

    Page<Plant> getUserPlants(int pageNumber);

    Page<Plant> getPlantsByVarietyAs(String variety, int pageNumber);

    Page<Plant> getPlantsBySpecies(Species species, int pageNumber);

    Page<Plant> getPlantsByVarietyAndSpecies(String variety, Species species, int pageNumber);

    Plant getPlantById(UUID uuid);

    Plant addPlant(Plant plant);

    Plant updatePlant(Plant plant) throws NoSuchObjectException;

    void deletePlantSafe(Plant plant);
}
