package org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class HarvestDTO extends AgriculturalOperationDTO {
    private ResourceType resourceType ;
    private float quantityPerAreaUnit;
    private UUID cropParameters;
}