package org.zeros.farm_manager_server.domain.dto.operations;

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
public class SprayApplicationDTO extends AgriculturalOperationDTO {
    private UUID spray;
    private UUID fertilizer;
    private BigDecimal quantityPerAreaUnit;
    private BigDecimal pricePerUnit;
    private BigDecimal fertilizerQuantityPerAreaUnit;
    private BigDecimal fertilizerPricePerUnit;
}
