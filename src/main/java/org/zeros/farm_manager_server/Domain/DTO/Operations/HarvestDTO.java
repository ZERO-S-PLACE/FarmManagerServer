package org.zeros.farm_manager_server.Domain.DTO.Operations;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class HarvestDTO extends AgriculturalOperationDTO {
    private ResourceType resourceType;
    private BigDecimal quantityPerAreaUnit;
    private UUID cropParameters;
}
