package org.zeros.farm_manager_server.domain.entities.crop;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.entities.BaseEntity;
import org.zeros.farm_manager_server.domain.entities.crop.crop_parameters.CropParameters;
import org.zeros.farm_manager_server.domain.enums.ResourceType;
import org.zeros.farm_manager_server.model.ApplicationDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CropSale extends BaseEntity {

    @Transient
    public static final CropSale NONE = CropSale.builder()
            .crop(MainCrop.NONE)
            .resourceType(ResourceType.NONE)
            .build();
    @NonNull
    @ManyToOne
    @Builder.Default
    private Crop crop = MainCrop.NONE;
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
}
