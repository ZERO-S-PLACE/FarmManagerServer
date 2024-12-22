package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
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
public class FertilizerApplication extends AgriculturalOperation {
    @NonNull
    @ManyToOne
    @Builder.Default
    private Fertilizer fertilizer=Fertilizer.NONE;
    @NonNull
    @Builder.Default
    private BigDecimal quantityPerAreaUnit=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    private BigDecimal pricePerUnit=BigDecimal.ZERO;

    @Transient
    public static final FertilizerApplication NONE =FertilizerApplication.builder().crop(MainCrop.NONE)
            .build();
    @PrePersist
    private void init() {
        setOperationType(OperationType.FERTILIZER_APPLICATION);
    }
}
