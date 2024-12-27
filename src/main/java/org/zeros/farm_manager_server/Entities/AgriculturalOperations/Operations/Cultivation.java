package org.zeros.farm_manager_server.Entities.AgriculturalOperations.Operations;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.CultivationType;
import org.zeros.farm_manager_server.Entities.AgriculturalOperations.Enum.OperationType;
import org.zeros.farm_manager_server.Entities.Crop.Crop.MainCrop;

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
    @Builder.Default
    @DecimalMin("0.0")
    private BigDecimal depth = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CultivationType cultivationType = CultivationType.NONE;

    @PostLoad
    @PostConstruct
    private void init() {
        setOperationType(OperationType.CULTIVATION);
    }

    @Transient
    public static final Cultivation NONE = Cultivation.builder()
            .crop(MainCrop.NONE)
            .build();

}
