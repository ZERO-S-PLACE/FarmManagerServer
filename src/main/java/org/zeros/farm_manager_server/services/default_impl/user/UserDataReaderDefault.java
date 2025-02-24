package org.zeros.farm_manager_server.services.default_impl.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.domain.dto.crop.CropDTO;
import org.zeros.farm_manager_server.domain.dto.operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.domain.dto.user.UserDTO;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.crop.MainCrop;
import org.zeros.farm_manager_server.domain.entities.fields.Field;
import org.zeros.farm_manager_server.domain.entities.fields.FieldPart;
import org.zeros.farm_manager_server.domain.entities.operations.*;
import org.zeros.farm_manager_server.domain.entities.user.User;
import org.zeros.farm_manager_server.domain.enums.OperationType;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.default_impl.fields.FieldManagerDefault;
import org.zeros.farm_manager_server.services.interfaces.crop.CropManager;
import org.zeros.farm_manager_server.services.interfaces.user.UserDataReader;
import org.zeros.farm_manager_server.services.interfaces.user.UserManager;

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
