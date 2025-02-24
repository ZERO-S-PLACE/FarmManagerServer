package org.zeros.farm_manager_server.domain.entities.operations;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.entities.crop.crop_parameters.CropParameters;
import org.zeros.farm_manager_server.domain.entities.crop.MainCrop;
import org.zeros.farm_manager_server.domain.enums.OperationType;
import org.zeros.farm_manager_server.domain.enums.ResourceType;

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
