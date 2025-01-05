package org.zeros.farm_manager_server.Domain.DTO.Data;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class FarmingMachineDTO extends BaseEntityDTO {

    @Transient
    public final static FarmingMachineDTO UNDEFINED = FarmingMachineDTO.builder()
            .producer("UNDEFINED")
            .model("UNDEFINED")
            .supportedOperationTypes(Set.of(OperationType.ANY))
            .build();
    private String producer;
    private String model;
    private Set<OperationType> supportedOperationTypes;
    private String description;
}
