package org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SprayApplicationDTO extends AgriculturalOperationDTO {
    private UUID spray;
    private UUID fertilizer;
    private float quantityPerAreaUnit;
    private float pricePerUnit;
    private float fertilizerQuantityPerAreaUnit;
    private float fertilizerPricePerUnit;
}
