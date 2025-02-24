package org.zeros.farm_manager_server.domain.entities.operations;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.entities.crop.MainCrop;
import org.zeros.farm_manager_server.domain.enums.CultivationType;
import org.zeros.farm_manager_server.domain.enums.OperationType;

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
    @PostUpdate
    private void init() {
        setOperationType(OperationType.CULTIVATION);
    }

}
