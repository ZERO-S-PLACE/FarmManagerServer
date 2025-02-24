package org.zeros.farm_manager_server.services.interfaces.crop;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.domain.dto.crop.CropDTO;
import org.zeros.farm_manager_server.domain.dto.crop.InterCropDTO;
import org.zeros.farm_manager_server.domain.dto.crop.MainCropDTO;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface CropManager {

    CropDTO getCropById(@NotNull UUID cropId);

    MainCropDTO createNewMainCrop(@NotNull UUID fieldPartId, @NotNull Set<UUID> cultivatedPlantsIds);

    InterCropDTO createNewInterCrop(@NotNull UUID fieldPartId, @NotNull Set<UUID> cultivatedPlantsIds);

    void updateCultivatedPlants(@NotNull UUID cropId, @NotNull Set<UUID> cultivatedPlantsIds);

    void addSubside(@NotNull UUID cropId, @NotNull UUID subsideId);

    void removeSubside(@NotNull UUID cropId, @NotNull UUID subsideId);

    void setDateDestroyed(@NotNull UUID interCropId, @NotNull LocalDate dateDestroyed);

    void setWorkFinished(@NotNull UUID cropId);

    void setFullySold(@NotNull UUID mainCropId);

    void deleteCropAndItsData(@NotNull UUID cropId);

    Crop getCropIfExists(UUID cropId);

}
