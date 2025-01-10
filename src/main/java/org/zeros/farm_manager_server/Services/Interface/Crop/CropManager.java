package org.zeros.farm_manager_server.Services.Interface.Crop;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.InterCropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.MainCropDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.InterCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;

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
