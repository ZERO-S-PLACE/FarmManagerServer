package org.zeros.farm_manager_server.Controllers.Data;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FarmingMachineController {
    public static final String BASE_PATH = "/api/user/farming_machine";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String LIST_ALL_PATH = BASE_PATH + "/ALL";
    public static final String LIST_USER_PATH = BASE_PATH + "/USER";
    public static final String LIST_DEFAULT_PATH = BASE_PATH + "/DEFAULT";
    public static final String LIST_PARAM_PATH = BASE_PATH + "/PARAM";
    private final FarmingMachineManager farmingMachineManager;

    @GetMapping(ID_PATH)
    public FarmingMachineDTO getById(@PathVariable("id") UUID id) {
        return farmingMachineManager.getFarmingMachineById(id);
    }

    @GetMapping(LIST_ALL_PATH)
    public Page<FarmingMachineDTO> getAll(@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return farmingMachineManager.getAllFarmingMachines(pageNumber);
    }

    @GetMapping(LIST_DEFAULT_PATH)
    public Page<FarmingMachineDTO> getDefault(@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return farmingMachineManager.getDefaultFarmingMachines(pageNumber);
    }

    @GetMapping(LIST_USER_PATH)
    public Page<FarmingMachineDTO> getUserCreated(@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return farmingMachineManager.getUserFarmingMachines(pageNumber);
    }

    @GetMapping(LIST_PARAM_PATH)
    public Page<FarmingMachineDTO> getCriteria(@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                               @RequestParam(required = false) String model,
                                               @RequestParam(required = false) String producer,
                                               @RequestParam(required = false) OperationType operationType) {
        return farmingMachineManager.getFarmingMachineCriteria(model, producer, operationType, pageNumber);
    }

    @PostMapping(BASE_PATH)
    ResponseEntity<String> addNew(@RequestBody FarmingMachineDTO farmingMachineDTO) {

        FarmingMachineDTO saved = farmingMachineManager.addFarmingMachine(farmingMachineDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PatchMapping(BASE_PATH)
    ResponseEntity<String> update(@RequestBody FarmingMachineDTO farmingMachineDTO) {
        farmingMachineManager.updateFarmingMachine(farmingMachineDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id) {
        farmingMachineManager.deleteFarmingMachineSafe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
