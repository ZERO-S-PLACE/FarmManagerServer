package org.zeros.farm_manager_server.DAO.Interface.Data;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.entities.Crop.Subside;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

public interface SubsideManager {
    Page<Subside> getAllSubsides(int pageNumber);

    Page<Subside> getDefaultSubsides(int pageNumber);

    Page<Subside> getUserSubsides(int pageNumber);

    Page<Subside> getSubsidesByNameAs(String name, int pageNumber);

    Page<Subside> getSubsidesBySpeciesAllowed(Species species, int pageNumber);

    Subside getSubsideById(UUID id);

    Subside addSubside(Subside subside);

    Subside updateSubside(Subside subside) throws NoSuchObjectException;

    void deleteSubsideSafe(Subside subside);


}
