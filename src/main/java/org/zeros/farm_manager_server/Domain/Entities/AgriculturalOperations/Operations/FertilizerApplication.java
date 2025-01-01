package org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.OperationType;

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
    private Fertilizer fertilizer = Fertilizer.NONE;

    @NonNull
    @Builder.Default
    private BigDecimal quantityPerAreaUnit = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private BigDecimal pricePerUnit = BigDecimal.ZERO;

    @PostLoad
    @PostConstruct
    private void init() {
        setOperationType(OperationType.FERTILIZER_APPLICATION);
    }

    @Transient
    public static final FertilizerApplication NONE = FertilizerApplication.builder()
            .crop(MainCrop.NONE)
            .build();
}
