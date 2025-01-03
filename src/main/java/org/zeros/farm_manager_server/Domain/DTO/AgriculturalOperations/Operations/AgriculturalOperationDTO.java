package org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class AgriculturalOperationDTO extends BaseEntityDTO {
    private UUID crop;
    private UUID farmingMachine;
    private OperationType operationType;
    private LocalDate dateStarted;
    private LocalDate dateFinished;
    private Boolean isExternalService;
    private float externalServicePrice;
    private float fuelConsumptionPerUnit;
    private float fuelPrice;
    private Boolean isPlannedOperation;

}