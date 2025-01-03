package org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class FarmingMachineDTO extends BaseEntityDTO {

    private String producer;
    private String model;
    private Set<OperationType> supportedOperationTypes;
    private String description;

    @Transient
    public final static FarmingMachineDTO UNDEFINED = FarmingMachineDTO.builder()
            .producer("UNDEFINED")
            .model("UNDEFINED")
            .supportedOperationTypes(Set.of(OperationType.ANY))
            .build();
}
