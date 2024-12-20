package org.zeros.farm_manager_server.entities.Crops;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.ResourceType;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crops.CropParameters.CropParameters;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CropSale extends DatabaseEntity {
    @NonNull
    @ManyToOne
    Crop crop;
    @NonNull
    @Builder.Default
    private LocalDate dateSold =LocalDate.MIN;
    @NonNull
    @Builder.Default
    private String soldTo="";
    @NonNull
    @DecimalMin("0.000")
    @Builder.Default
    private BigDecimal amountSold = BigDecimal.ZERO;
    @NonNull
    @DecimalMin("0.000")
    @Builder.Default
    private BigDecimal pricePerUnit = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType=ResourceType.GRAIN;
    @NonNull
    @Builder.Default
    private String unit="t";
    @NonNull
    @ManyToOne
    @Builder.Default
    private CropParameters cropParameters=CropParameters.NONE;
}
