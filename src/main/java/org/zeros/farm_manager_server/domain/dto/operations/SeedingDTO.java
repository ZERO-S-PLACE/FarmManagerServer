package org.zeros.farm_manager_server.domain.dto.operations;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SeedingDTO extends AgriculturalOperationDTO {
    private Set<UUID> sownPlants;
    private BigDecimal depth;
    private BigDecimal rowSpacing;
    private BigDecimal quantityPerAreaUnit;
    private BigDecimal germinationRate;
    private BigDecimal materialPurity;
    private BigDecimal thousandSeedsMass;
    private BigDecimal seedsPerAreaUnit;
    private BigDecimal seedsCostPerUnit;
}
