package org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations;

import lombok.*;
import lombok.experimental.SuperBuilder;

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
    private float depth;
    private float rowSpacing;
    private float quantityPerAreaUnit;
    private float germinationRate;
    private float materialPurity;
    private float thousandSeedsMass;
    private float seedsPerAreaUnit;
    private float seedsCostPerUnit;
}
