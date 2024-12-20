package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Seeding extends AgriculturalOperation {
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal depth=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal rowSpacing=BigDecimal.valueOf(10);
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal quantityPerAreaUnit=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal germinationRate=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal materialPurity=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal thousandSeedsMass=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal seedsPerAreaUnit=BigDecimal.ZERO;


}
