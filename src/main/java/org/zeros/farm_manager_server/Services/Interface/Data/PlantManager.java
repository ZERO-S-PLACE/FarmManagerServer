package org.zeros.farm_manager_server.Services.Interface.Data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.Crop.Plant.PlantDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;

import java.util.UUID;

public interface PlantManager {

    Page<Plant> getAllPlants(int pageNumber);

    Page<Plant> getDefaultPlants(int pageNumber);

    Page<Plant> getUserPlants(int pageNumber);

    Page<Plant> getPlantsByVarietyAs(@NotNull String variety, int pageNumber);

    Page<Plant> getPlantsBySpecies(@NotNull Species species, int pageNumber);

    Page<Plant> getPlantsByVarietyAndSpecies(@NotNull String variety,@NotNull Species species, int pageNumber);

    Page<Plant> getPlantsCriteria( String variety,  UUID speciesId, int pageNumber);

    Plant getPlantById(@NotNull UUID uuid);

    Plant addPlant(@NotNull PlantDTO plantDTO);

    Plant updatePlant(@NotNull PlantDTO plantDTO);

    void deletePlantSafe(@NotNull UUID plantId);


}
