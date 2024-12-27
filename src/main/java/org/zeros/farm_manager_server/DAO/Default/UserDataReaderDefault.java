package org.zeros.farm_manager_server.DAO.Default;

import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.UserDataReader;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.entities.Fields.Field;
import org.zeros.farm_manager_server.entities.Fields.FieldPart;
import org.zeros.farm_manager_server.entities.User.User;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserDataReaderDefault implements UserDataReader {
    private final UserManager userManager;
    private final LoggedUserConfiguration loggedUserConfiguration;

    public UserDataReaderDefault(UserManager userManager, LoggedUserConfiguration config) {
        this.userManager = userManager;
        this.loggedUserConfiguration = config;
    }

    @Override
    public Set<AgriculturalOperation> getAllPlannedOperations(OperationType operationType) {
        Set<AgriculturalOperation> allPlannedOperations = new HashSet<>();
        for (Crop crop : getAllActiveCrops()) {
            if (operationType.equals(OperationType.SEEDING) || operationType.equals(OperationType.ANY)) {
                for (Seeding seeding : crop.getSeeding()) {
                    if (seeding.getIsPlannedOperation()) {
                        allPlannedOperations.add(seeding);
                    }
                }
            }
            if (operationType.equals(OperationType.CULTIVATION) || operationType.equals(OperationType.ANY)) {
                for (Cultivation cultivation : crop.getCultivations()) {
                    if (cultivation.getIsPlannedOperation()) {
                        allPlannedOperations.add(cultivation);
                    }
                }
            }
            if (operationType.equals(OperationType.FERTILIZER_APPLICATION) || operationType.equals(OperationType.ANY)) {
                for (FertilizerApplication fertilizerApplication : crop.getFertilizerApplications()) {
                    if (fertilizerApplication.getIsPlannedOperation()) {
                        allPlannedOperations.add(fertilizerApplication);
                    }
                }
            }
            if (operationType.equals(OperationType.SPRAY_APPLICATION) || operationType.equals(OperationType.ANY)) {
                for (SprayApplication sprayApplication : crop.getSprayApplications()) {
                    if (sprayApplication.getIsPlannedOperation()) {
                        allPlannedOperations.add(sprayApplication);
                    }
                }
            }
            if (crop instanceof MainCrop) {
                if (operationType.equals(OperationType.HARVEST) || operationType.equals(OperationType.ANY)) {
                    for (Harvest harvest : ((MainCrop) crop).getHarvest()) {
                        if (harvest.getIsPlannedOperation()) {
                            allPlannedOperations.add(harvest);
                        }
                    }
                }
            }
        }


        return allPlannedOperations;
    }


    @Override
    public Set<Crop> getAllActiveCrops() {
        User user = userManager.getUserById(loggedUserConfiguration.getLoggedUserProperty().get().getId());
        Set<Crop> activeCrops = new HashSet<>();
        for (Field field : user.getFields()) {
            for (FieldPart fieldPart : field.getFieldParts()) {
                for (Crop crop : fieldPart.getCrops()) {
                    if (!crop.getWorkFinished()) {
                        activeCrops.add(crop);
                    }
                }
            }
        }
        return activeCrops;
    }

    @Override
    public Set<Crop> getAllUnsoldCrops() {
        User user = userManager.getUserById(loggedUserConfiguration.getLoggedUserProperty().get().getId());
        Set<Crop> unsoldCrops = new HashSet<>();
        for (Field field : user.getFields()) {
            for (FieldPart fieldPart : field.getFieldParts()) {
                for (Crop crop : fieldPart.getCrops()) {
                    if (crop.getWorkFinished() && crop instanceof MainCrop) {
                        if (!((MainCrop) crop).getIsFullySold()) {
                            unsoldCrops.add(crop);
                        }
                    }
                }
            }
        }
        return unsoldCrops;
    }
}
