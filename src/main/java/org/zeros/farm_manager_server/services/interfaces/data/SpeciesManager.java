package org.zeros.farm_manager_server.services.interfaces.data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.domain.dto.data.SpeciesDTO;
import org.zeros.farm_manager_server.domain.entities.data.Species;

import java.util.UUID;

public interface SpeciesManager {

    Page<SpeciesDTO> getAllSpecies(int pageNumber);

    Page<SpeciesDTO> getDefaultSpecies(int pageNumber);

    Page<SpeciesDTO> getUserSpecies(int pageNumber);

    Page<SpeciesDTO> getSpeciesByNameAs(@NotNull String name, int pageNumber);

    Page<SpeciesDTO> getSpeciesByFamilyAs(@NotNull String family, int pageNumber);

    Page<SpeciesDTO> getSpeciesCriteria(String name, String family, int pageNumber);

    SpeciesDTO getSpeciesById(@NotNull UUID id);

    SpeciesDTO addSpecies(@NotNull SpeciesDTO speciesDTO);

    SpeciesDTO updateSpecies(@NotNull SpeciesDTO speciesDTO);

    void deleteSpeciesSafe(@NotNull UUID speciesId);

    Species getSpeciesIfExists(UUID species);

    Species getUndefinedSpecies();


}
