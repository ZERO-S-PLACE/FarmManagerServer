package org.zeros.farm_manager_server.Services.Default.User;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.Operations.*;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Services.Interface.User.UserDataReader;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.util.HashSet;
import java.util.Set;

@Service
@Primary
@RequiredArgsConstructor
public class UserDataReaderDefault implements UserDataReader {
    private final UserManager userManager;
    private final LoggedUserConfiguration loggedUserConfiguration;


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
        User user = userManager.getUserById(loggedUserConfiguration.getLoggedUser().getId());
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
        User user = userManager.getUserById(loggedUserConfiguration.getLoggedUser().getId());
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
