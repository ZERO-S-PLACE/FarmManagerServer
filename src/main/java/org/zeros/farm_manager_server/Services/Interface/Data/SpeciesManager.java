package org.zeros.farm_manager_server.Services.Interface.Data;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.Crop.Plant.SpeciesDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

public interface SpeciesManager {

    Page<Species> getAllSpecies(int pageNumber);

    Page<Species> getDefaultSpecies(int pageNumber);

    Page<Species> getUserSpecies(int pageNumber);

    Page<Species> getSpeciesByNameAs(String name, int pageNumber);

    Page<Species> getSpeciesByFamilyAs(String family, int pageNumber);

    Species getSpeciesById(UUID id);

    Species addSpecies(SpeciesDTO speciesDTO);

    Species updateSpecies(SpeciesDTO speciesDTO);

    void deleteSpeciesSafe(UUID speciesId);

    Species getUndefinedSpecies();

}
