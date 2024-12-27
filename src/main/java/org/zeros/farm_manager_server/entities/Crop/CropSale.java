package org.zeros.farm_manager_server.entities.Crop;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
import org.zeros.farm_manager_server.model.ApplicationDefaults;

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
    @Builder.Default
    Crop crop = MainCrop.NONE;

    @NonNull
    @Builder.Default
    private LocalDate dateSold = ApplicationDefaults.UNDEFINED_DATE_MIN;

    @NonNull
    @Builder.Default
    private String soldTo = "";

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
    private ResourceType resourceType = ResourceType.GRAIN;

    @NonNull
    @Builder.Default
    private String unit = "t";

    @NonNull
    @ManyToOne
    @Builder.Default
    private CropParameters cropParameters = CropParameters.NONE;

    @Transient
    public static final CropSale NONE = CropSale.builder()
            .crop(MainCrop.NONE)
            .resourceType(ResourceType.NONE)
            .build();
}