package org.zeros.farm_manager_server.Domain.DTO.Operations;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class FertilizerApplicationDTO extends AgriculturalOperationDTO {
    private UUID fertilizer;
    private BigDecimal quantityPerAreaUnit;
    private BigDecimal pricePerUnit;
}
