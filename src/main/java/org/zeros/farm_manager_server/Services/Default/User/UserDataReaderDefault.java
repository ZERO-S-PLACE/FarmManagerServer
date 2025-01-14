package org.zeros.farm_manager_server.Services.Default.User;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserDataReader;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class UserDataReaderDefault implements UserDataReader {
    private final UserManager userManager;
    private final LoggedUserConfiguration loggedUserConfiguration;
    private final UserRepository userRepository;
    private final FieldManagerDefault fieldManagerDefault;
    private final CropManager cropManager;


    @Override
    public Set<AgriculturalOperationDTO> getAllPlannedOperations(OperationType operationType) {
        Set<AgriculturalOperation> allPlannedOperations = new HashSet<>();
        for (CropDTO cropDTO : getAllActiveCrops()) {
            Crop crop = cropManager.getCropIfExists(cropDTO.getId());
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


        return allPlannedOperations.stream().map(DefaultMappers.agriculturalOperationMapper::entityToDto).collect(Collectors.toSet());
    }


    @Override
    public Set<CropDTO> getAllActiveCrops() {
        User user = userRepository.findById(loggedUserConfiguration.getLoggedUser().getId())
                .orElseThrow(() -> new IllegalArgumentExceptionCustom(User.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
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
        return activeCrops.stream().map(DefaultMappers.cropMapper::entityToDto).collect(Collectors.toSet());
    }

    @Override
    public Set<CropDTO> getAllUnsoldCrops() {
        UserDTO user = userManager.getUserById(loggedUserConfiguration.getLoggedUser().getId());
        Set<Crop> unsoldCrops = new HashSet<>();
        for (UUID fieldId : user.getFields()) {
            Field field = fieldManagerDefault.getFieldIfExists(fieldId);
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
        return unsoldCrops.stream().map(DefaultMappers.cropMapper::entityToDto).collect(Collectors.toSet());
    }
}
