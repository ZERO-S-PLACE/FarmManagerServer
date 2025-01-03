package org.zeros.farm_manager_server.Services.Interface.Data;

import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.Domain.DTO.Crop.SubsideDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Subside;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

public interface SubsideManager {
    Page<Subside> getAllSubsides(int pageNumber);

    Page<Subside> getDefaultSubsides(int pageNumber);

    Page<Subside> getUserSubsides(int pageNumber);

    Page<Subside> getSubsidesByNameAs(String name, int pageNumber);

    Page<Subside> getSubsidesBySpeciesAllowed(Species species, int pageNumber);

    Subside getSubsideById(UUID id);

    Subside addSubside(SubsideDTO subsideDTO);

    Subside updateSubside(SubsideDTO subsideDAO) throws NoSuchObjectException;

    void deleteSubsideSafe(UUID subsideId);


}
