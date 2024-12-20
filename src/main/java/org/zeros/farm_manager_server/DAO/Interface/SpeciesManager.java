package org.zeros.farm_manager_server.DAO.Interface;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

public interface SpeciesManager {

    Page<Species> getAllSpecies(int pageNumber);
    Page<Species> getDefaultSpecies(int pageNumber);
    Page<Species> getUserSpecies(int pageNumber);
    Page<Species> getSpeciesByNameAs(String name,int pageNumber);
    Page<Species> getSpeciesByFamilyAs(String family, int pageNumber);
    Species getSpeciesById(UUID id);
    Species addSpecies(Species species);
    Species updateSpecies(Species species) throws NoSuchObjectException;
    void deleteSpeciesSafe(Species species);

}
