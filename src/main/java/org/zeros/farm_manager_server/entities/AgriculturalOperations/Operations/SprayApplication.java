package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"spray","fertilizer"})
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SprayApplication extends AgriculturalOperation {
    @NonNull
    @ManyToOne
    private Spray spray;
    @NonNull
    @ManyToOne
    @Builder.Default
    private Fertilizer fertilizer=Fertilizer.NONE;
    @NonNull
    @DecimalMin("0.000")
    @Builder.Default
    private BigDecimal quantityPerAreaUnit=BigDecimal.ZERO;
    @NonNull
    @DecimalMin("0.000")
    @Builder.Default
    private BigDecimal pricePerUnit=BigDecimal.ZERO;
    @NonNull
    @DecimalMin("0.000")
    @Builder.Default
    private BigDecimal fertilizerQuantityPerAreaUnit=BigDecimal.ZERO;
    @NonNull
    @DecimalMin("0.000")
    @Builder.Default
    private BigDecimal fertilizerPricePerUnit=BigDecimal.ZERO;



}
