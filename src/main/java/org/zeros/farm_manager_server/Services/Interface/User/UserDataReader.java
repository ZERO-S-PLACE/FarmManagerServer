package org.zeros.farm_manager_server.Services.Interface.User;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;

import java.util.Set;

public interface UserDataReader {

    Set<AgriculturalOperationDTO> getAllPlannedOperations(@NotNull OperationType operationType);

    Set<CropDTO> getAllActiveCrops();

    Set<CropDTO> getAllUnsoldCrops();


}
