package org.zeros.farm_manager_server.Services.Interface.Data;

import jakarta.validation.constraints.NotNull;
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

    Page<Subside> getSubsidesByNameAs(@NotNull String name, int pageNumber);

    Page<Subside> getSubsidesBySpeciesAllowed(@NotNull UUID speciesId, int pageNumber);

    Page<Subside> getSubsidesByNameAsAndSpeciesAllowed(@NotNull String name,@NotNull UUID speciesId, int pageNumber);

    Page<Subside> getSubsidesCriteria(String name, UUID speciesId, int pageNumber);

    Subside getSubsideById(@NotNull UUID id);

    Subside addSubside(@NotNull SubsideDTO subsideDTO);

    Subside updateSubside(@NotNull SubsideDTO subsideDTO) ;

    void deleteSubsideSafe(@NotNull UUID subsideId);


}
