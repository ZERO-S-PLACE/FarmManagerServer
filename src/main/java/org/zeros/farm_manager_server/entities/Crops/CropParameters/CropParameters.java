package org.zeros.farm_manager_server.entities.Crops.CropParameters;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.ResourceType;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "crop_type",discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("COMMENT")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CropParameters extends DatabaseEntity {

    @NonNull
    @Builder.Default
    private String name = "";
    @NonNull
    @Builder.Default
    private String createdBy = "";
    @NonNull
    @Builder.Default
    private String comment = "";
    @NonNull
    @Builder.Default
    @DecimalMin("0.00")
    private BigDecimal pollution=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType=ResourceType.GRAIN;

    @Transient
    public static final CropParameters NONE =CropParameters.builder()
            .name("NONE")
            .createdBy("NONE")
            .resourceType(ResourceType.NONE)
            .comment("NONE")
            .build();
    @Transient
    public static final CropParameters UNDEFINED =CropParameters.builder()
            .name("UNDEFINED")
            .createdBy("UNDEFINED")
            .resourceType(ResourceType.ANY)
            .comment("UNDEFINED")
            .build();
}
