package org.zeros.farm_manager_server.services.interfaces.data;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.zeros.farm_manager_server.domain.dto.data.SubsideDTO;
import org.zeros.farm_manager_server.domain.entities.data.Subside;

import java.util.UUID;

public interface SubsideManager {
    Page<SubsideDTO> getAllSubsides(int pageNumber);

    Page<SubsideDTO> getDefaultSubsides(int pageNumber);

    Page<SubsideDTO> getUserSubsides(int pageNumber);

    Page<SubsideDTO> getSubsidesByNameAs(@NotNull String name, int pageNumber);

    Page<SubsideDTO> getSubsidesBySpeciesAllowed(@NotNull UUID speciesId, int pageNumber);

    Page<SubsideDTO> getSubsidesByNameAsAndSpeciesAllowed(@NotNull String name, @NotNull UUID speciesId, int pageNumber);

    Page<SubsideDTO> getSubsidesCriteria(String name, UUID speciesId, int pageNumber);

    SubsideDTO getSubsideById(@NotNull UUID id);

    SubsideDTO addSubside(@NotNull SubsideDTO subsideDTO);

    SubsideDTO updateSubside(@NotNull SubsideDTO subsideDTO);

    void deleteSubsideSafe(@NotNull UUID subsideId);

    Subside getSubsideIfExists(UUID subsideId);

}
