package org.zeros.farm_manager_server.Controllers.Operations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Domain.DTO.Operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Services.Interface.Operations.AgriculturalOperationsManager;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AgriculturalOperationsController {
    public static final String BASE_PATH = "/api/user/crop/operation";
    public static final String ID_PATH = BASE_PATH + "/{id}";
    public static final String PLAN_OPERATION_PATH = BASE_PATH + "/PLAN";
    public static final String ADD_OPERATION_PATH = BASE_PATH + "/ADD";
    public static final String OPERATION_MACHINE_PATH = BASE_PATH + "/UPDATE_MACHINE";
    private final AgriculturalOperationsManager operationsManager;


    @GetMapping(ID_PATH)
    public AgriculturalOperationDTO getOperationById(@PathVariable("id") UUID id, @RequestParam OperationType type) {
        return operationsManager.getOperationById(id, type);
    }

    @PostMapping(PLAN_OPERATION_PATH)
    ResponseEntity<String> planOperation(@RequestParam UUID cropId, @RequestBody AgriculturalOperationDTO operationDTO) {
        AgriculturalOperationDTO saved = operationsManager.planOperation(cropId, operationDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PostMapping(ADD_OPERATION_PATH)
    ResponseEntity<String> addOperation(@RequestParam UUID cropId, @RequestBody AgriculturalOperationDTO operationDTO) {
        AgriculturalOperationDTO saved = operationsManager.addOperation(cropId, operationDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BASE_PATH + "/" + saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PatchMapping(ADD_OPERATION_PATH)
    ResponseEntity<String> setPlannedOperationPerformed(@RequestParam UUID cropId, @RequestParam OperationType type) {
        operationsManager.setPlannedOperationPerformed(cropId, type);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(OPERATION_MACHINE_PATH)
    ResponseEntity<String> updateOperationMachine(@RequestParam UUID cropId, @RequestParam OperationType type,
                                                  @RequestParam UUID farmingMachineId) {
        operationsManager.updateOperationMachine(cropId, type, farmingMachineId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(BASE_PATH)
    ResponseEntity<String> updateOperationParameters(@RequestBody AgriculturalOperationDTO operationDTO) {
        operationsManager.updateOperationParameters(operationDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BASE_PATH)
    ResponseEntity<String> deleteById(@RequestParam UUID id, @RequestParam OperationType type) {
        operationsManager.deleteOperation(id, type);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
