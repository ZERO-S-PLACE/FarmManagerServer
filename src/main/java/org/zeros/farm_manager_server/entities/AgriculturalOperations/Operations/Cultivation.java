package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.CultivationType;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Cultivation extends AgriculturalOperation {
    @NonNull
    @DecimalMin("0.0")
    @Builder.Default
    private BigDecimal depth=BigDecimal.ZERO;
    @NonNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CultivationType cultivationType=CultivationType.NONE;
}
