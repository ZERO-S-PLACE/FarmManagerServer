package org.zeros.farm_manager_server.DAO.Interface;

import org.zeros.farm_manager_server.entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.entities.Crop.Crop.Crop;

import java.util.Set;

public interface UserDataReader {

    Set<AgriculturalOperation> getAllPlannedOperations(OperationType operationType);

    Set<Crop> getAllActiveCrops();

    Set<Crop> getAllUnsoldCrops();


}
