package org.zeros.farm_manager_server.Domain.DTO.Operations;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;

import java.math.BigDecimal;
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
    private BigDecimal externalServicePrice;
    private BigDecimal fuelConsumptionPerUnit;
    private BigDecimal fuelPrice;
    private Boolean isPlannedOperation;

}