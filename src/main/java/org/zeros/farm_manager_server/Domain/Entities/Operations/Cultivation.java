package org.zeros.farm_manager_server.Domain.Entities.Operations;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.Enum.CultivationType;
import org.zeros.farm_manager_server.Domain.Entities.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Cultivation extends AgriculturalOperation {

    @Transient
    public static final Cultivation NONE = Cultivation.builder()
            .crop(MainCrop.NONE)
            .build();
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

}
