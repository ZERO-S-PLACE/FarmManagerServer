package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.CultivationType;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.OperationType;
import org.zeros.farm_manager_server.entities.Crops.Crop.MainCrop;

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
    @Transient
    public static final Cultivation NONE =Cultivation.builder().crop(MainCrop.NONE)
            .build();
    @PrePersist
    private void init() {
        setOperationType(OperationType.CULTIVATION);
    }

}
