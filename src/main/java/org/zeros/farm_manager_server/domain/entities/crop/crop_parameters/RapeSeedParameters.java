package org.zeros.farm_manager_server.domain.entities.crop.crop_parameters;

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
@DiscriminatorValue("RAPESEED")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RapeSeedParameters extends CropParameters {

    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal density = BigDecimal.valueOf(650);

    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal humidity = BigDecimal.valueOf(8);

    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal oilContent = BigDecimal.valueOf(40);

}
