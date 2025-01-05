package org.zeros.farm_manager_server.Services.Interface.Crop;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.InterCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface CropManager {
    Crop getCropById(@NotNull UUID cropId);

    MainCrop createNewMainCrop(@NotNull UUID fieldPartId, @NotNull Set<UUID> cultivatedPlantsIds);

    InterCrop createNewInterCrop(@NotNull UUID fieldPartId, @NotNull Set<UUID> cultivatedPlantsIds);

    void updateCultivatedPlants(@NotNull UUID cropId, @NotNull Set<UUID> cultivatedPlantsIds);

    void addSubside(@NotNull UUID cropId, @NotNull UUID subsideId);

    void removeSubside(@NotNull UUID cropId, @NotNull UUID subsideId);

    Crop getCropIfExists(UUID cropId);

    void setDateDestroyed(@NotNull UUID interCropId, @NotNull LocalDate dateDestroyed);

    void setWorkFinished(@NotNull UUID cropId);

    void setFullySold(@NotNull UUID mainCropId);

    void deleteCropAndItsData(@NotNull UUID cropId);

}
