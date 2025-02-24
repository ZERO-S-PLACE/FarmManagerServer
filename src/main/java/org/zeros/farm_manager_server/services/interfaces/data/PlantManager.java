package org.zeros.farm_manager_server.services.interfaces.data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.domain.dto.data.PlantDTO;
import org.zeros.farm_manager_server.domain.entities.data.Plant;
import org.zeros.farm_manager_server.domain.entities.data.Species;

import java.util.UUID;

public interface PlantManager {

    Page<PlantDTO> getAllPlants(int pageNumber);

    Page<PlantDTO> getDefaultPlants(int pageNumber);

    Page<PlantDTO> getUserPlants(int pageNumber);

    Page<PlantDTO> getPlantsByVarietyAs(@NotNull String variety, int pageNumber);

    Page<PlantDTO> getPlantsBySpecies(@NotNull Species species, int pageNumber);

    Page<PlantDTO> getPlantsByVarietyAndSpecies(@NotNull String variety, @NotNull Species species, int pageNumber);

    Page<PlantDTO> getPlantsCriteria(String variety, UUID speciesId, int pageNumber);

    PlantDTO getPlantById(@NotNull UUID uuid);

    PlantDTO addPlant(@NotNull PlantDTO plantDTO);

    PlantDTO updatePlant(@NotNull PlantDTO plantDTO);

    void deletePlantSafe(@NotNull UUID plantId);

    Plant getPlantIfExists(UUID uuid);


}
