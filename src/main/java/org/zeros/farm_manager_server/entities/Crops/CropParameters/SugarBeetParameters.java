package org.zeros.farm_manager_server.entities.Crops.CropParameters;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("SUGAR_BEET")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SugarBeetParameters extends CropParameters {
    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal sugar_content=BigDecimal.valueOf(40);
}
