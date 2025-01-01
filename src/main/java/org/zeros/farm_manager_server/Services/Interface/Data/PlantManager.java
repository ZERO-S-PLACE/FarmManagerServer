package org.zeros.farm_manager_server.Services.Interface.Data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Range;
import org.springframework.security.core.parameters.P;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;

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

    Plant updatePlant(Plant plant);

    void deletePlantSafe(Plant plant);

    Page<Plant> getPlantsCriteria(String variety, UUID speciesId, Integer pageNumber);
}
