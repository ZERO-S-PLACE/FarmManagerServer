package org.zeros.farm_manager_server.Services.Interface.User;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.Entities.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;

import java.util.Set;

public interface UserDataReader {

    Set<AgriculturalOperation> getAllPlannedOperations(@NotNull OperationType operationType);

    Set<Crop> getAllActiveCrops();

    Set<Crop> getAllUnsoldCrops();


}
