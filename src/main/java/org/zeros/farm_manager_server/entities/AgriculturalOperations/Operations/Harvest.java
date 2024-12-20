package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.ResourceType;
import org.zeros.farm_manager_server.entities.Crops.CropParameters.CropParameters;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Harvest extends AgriculturalOperation {
    @NonNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType=ResourceType.GRAIN;
    @NonNull
    @DecimalMin("0.00")
    @Builder.Default
    private BigDecimal quantityPerAreaUnit=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @ManyToOne
    private CropParameters cropParameters=CropParameters.NONE;
}
