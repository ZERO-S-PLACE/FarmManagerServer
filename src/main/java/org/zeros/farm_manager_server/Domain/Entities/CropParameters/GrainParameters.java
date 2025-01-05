package org.zeros.farm_manager_server.Domain.Entities.CropParameters;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("GRAIN")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GrainParameters extends CropParameters {

    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal glutenContent = BigDecimal.valueOf(10);

    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal proteinContent = BigDecimal.valueOf(12);

    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal fallingNumber = BigDecimal.valueOf(250);

    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal density = BigDecimal.valueOf(750);

    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal humidity = BigDecimal.valueOf(14);

}
