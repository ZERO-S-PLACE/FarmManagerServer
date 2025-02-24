package org.zeros.farm_manager_server.domain.dto.operations;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.enums.ResourceType;

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
