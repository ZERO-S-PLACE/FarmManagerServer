package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.OperationType;
import org.zeros.farm_manager_server.entities.Crops.Crop.MainCrop;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Seeding extends AgriculturalOperation {

    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal depth=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal rowSpacing=BigDecimal.valueOf(10);
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal quantityPerAreaUnit=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal germinationRate=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal materialPurity=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal thousandSeedsMass=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal seedsPerAreaUnit=BigDecimal.ZERO;

    @PrePersist
    private void init() {
        setOperationType(OperationType.SEEDING);
    }
    @Transient
    public static final Seeding NONE =Seeding.builder().crop(MainCrop.NONE)
            .build();


}
