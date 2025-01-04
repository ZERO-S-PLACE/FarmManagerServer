package org.zeros.farm_manager_server.Services.Interface.Data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.Crop.Plant.SpeciesDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

public interface SpeciesManager {

    Page<Species> getAllSpecies(int pageNumber);

    Page<Species> getDefaultSpecies(int pageNumber);

    Page<Species> getUserSpecies(int pageNumber);

    Page<Species> getSpeciesByNameAs(@NotNull String name, int pageNumber);

    Page<Species> getSpeciesByFamilyAs(@NotNull String family, int pageNumber);

    Page<Species> getSpeciesCriteria(String name, String family, int pageNumber);

    Species getSpeciesById(@NotNull UUID id);

    Species addSpecies(@NotNull SpeciesDTO speciesDTO);

    Species updateSpecies(@NotNull SpeciesDTO speciesDTO);

    void deleteSpeciesSafe(@NotNull UUID speciesId);

    Species getUndefinedSpecies();


}
