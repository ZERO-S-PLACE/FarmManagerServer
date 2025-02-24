package org.zeros.farm_manager_server.services.interfaces.user;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.domain.dto.crop.CropDTO;
import org.zeros.farm_manager_server.domain.dto.operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.domain.enums.OperationType;

import java.util.Set;

public interface UserDataReader {

    Set<AgriculturalOperationDTO> getAllPlannedOperations(@NotNull OperationType operationType);

    Set<CropDTO> getAllActiveCrops();

    Set<CropDTO> getAllUnsoldCrops();


}
