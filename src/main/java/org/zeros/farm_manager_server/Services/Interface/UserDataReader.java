package org.zeros.farm_manager_server.Services.Interface;

import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Entities.Crop.Crop.Crop;

import java.util.Set;

public interface UserDataReader {

    Set<AgriculturalOperation> getAllPlannedOperations(OperationType operationType);

    Set<Crop> getAllActiveCrops();

    Set<Crop> getAllUnsoldCrops();


}
