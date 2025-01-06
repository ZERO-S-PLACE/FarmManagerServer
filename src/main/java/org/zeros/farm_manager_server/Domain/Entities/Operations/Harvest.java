package org.zeros.farm_manager_server.Domain.Entities.Operations;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Harvest extends AgriculturalOperation {

    @Transient
    public static final Harvest NONE = Harvest.builder()
            .crop(MainCrop.NONE)
            .build();
    @NonNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType = ResourceType.GRAIN;
    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal quantityPerAreaUnit = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @ManyToOne
    private CropParameters cropParameters = CropParameters.NONE;

    @PostLoad
    @PostConstruct
    private void init() {
        setOperationType(OperationType.HARVEST);
    }


}
