package org.zeros.farm_manager_server.Domain.Entities.Operations;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Entities.Data.Spray;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"spray", "fertilizer"})
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SprayApplication extends AgriculturalOperation {

    @Transient
    public static final SprayApplication NONE = SprayApplication.builder()
            .crop(MainCrop.NONE)
            .build();
    @NonNull
    @ManyToOne
    @Builder.Default
    private Spray spray = Spray.NONE;
    @NonNull
    @ManyToOne
    @Builder.Default
    private Fertilizer fertilizer = Fertilizer.NONE;
    @NonNull
    @DecimalMin("0.000")
    @Builder.Default
    private BigDecimal quantityPerAreaUnit = BigDecimal.ZERO;
    @NonNull
    @DecimalMin("0.000")
    @Builder.Default
    private BigDecimal pricePerUnit = BigDecimal.ZERO;
    @NonNull
    @DecimalMin("0.000")
    @Builder.Default
    private BigDecimal fertilizerQuantityPerAreaUnit = BigDecimal.ZERO;
    @NonNull
    @DecimalMin("0.000")
    @Builder.Default
    private BigDecimal fertilizerPricePerUnit = BigDecimal.ZERO;

    @PostLoad
    @PostConstruct
    private void init() {
        setOperationType(OperationType.SPRAY_APPLICATION);
    }

}
