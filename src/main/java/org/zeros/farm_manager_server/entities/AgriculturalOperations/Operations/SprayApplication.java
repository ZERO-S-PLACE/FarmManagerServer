package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.OperationType;
import org.zeros.farm_manager_server.entities.Crops.Crop.MainCrop;

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
    @Builder.Default
    private Spray spray=Spray.NONE;
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
    @PrePersist
    private void init() {
        setOperationType(OperationType.SPRAY_APPLICATION);
    }

    @Transient
    public static final SprayApplication NONE =SprayApplication.builder().crop(MainCrop.NONE)
            .build();



}
