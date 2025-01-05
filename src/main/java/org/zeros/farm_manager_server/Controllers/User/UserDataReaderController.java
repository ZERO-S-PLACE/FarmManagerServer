package org.zeros.farm_manager_server.Controllers.User;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeros.farm_manager_server.Domain.DTO.Operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Interface.User.UserDataReader;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserDataReaderController {
    public static final String BASE_PATH = "/api/user";
    public static final String PLANNED_OPERATIONS_PATH = BASE_PATH + "/operations/PLANNED";
    public static final String ACTIVE_CROPS_PATH = BASE_PATH + "/ACTIVE_CROPS";
    public static final String UNSOLD_CROPS_PATH = BASE_PATH + "/UNSOLD_CROPS";
    private final UserDataReader userDataReader;

    @GetMapping(PLANNED_OPERATIONS_PATH)
    Set<AgriculturalOperationDTO> getAllPlannedOperations(@RequestParam(required = false, defaultValue = "ANY") OperationType operationType) {
        return userDataReader.getAllPlannedOperations(operationType).stream().map(DefaultMappers.agriculturalOperationMapper::entityToDto).collect(Collectors.toSet());
    }

    @GetMapping(ACTIVE_CROPS_PATH)
    Set<CropDTO> getAllActiveCrops() {
        return userDataReader
                .getAllActiveCrops()
                .stream().map(DefaultMappers.cropMapper::entityToDto)
                .collect(Collectors.toSet());
    }

    @GetMapping(UNSOLD_CROPS_PATH)
    Set<CropDTO> getAllUnsoldCrops() {
        return userDataReader.getAllUnsoldCrops().stream().map(DefaultMappers.cropMapper::entityToDto).collect(Collectors.toSet());
    }
}
