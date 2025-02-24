package org.zeros.farm_manager_server.domain.entities.operations;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.entities.crop.MainCrop;
import org.zeros.farm_manager_server.domain.entities.data.Plant;
import org.zeros.farm_manager_server.domain.enums.OperationType;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Seeding extends AgriculturalOperation {

    @Transient
    public static final Seeding NONE = Seeding.builder().crop(MainCrop.NONE)
            .build();
    @NonNull
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "seeding_plant", joinColumns = @JoinColumn(name = "seeding_id"), inverseJoinColumns = @JoinColumn(name = "plant_id"))
    private Set<Plant> sownPlants = new HashSet<>();
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal depth = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal rowSpacing = BigDecimal.valueOf(10);
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal quantityPerAreaUnit = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    @DecimalMax("1.000")
    private BigDecimal germinationRate = BigDecimal.valueOf(0.97);
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    @DecimalMax("1.000")
    private BigDecimal materialPurity = BigDecimal.valueOf(0.97);
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal thousandSeedsMass = BigDecimal.valueOf(40);
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal seedsPerAreaUnit = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    @DecimalMin("0.000")
    private BigDecimal seedsCostPerUnit = BigDecimal.ZERO;

    @PrePersist
    private void init() {
        if (seedsPerAreaUnit.equals(BigDecimal.ZERO) && quantityPerAreaUnit.equals(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("Seeding with no material");
        }
        if (quantityPerAreaUnit.equals(BigDecimal.ZERO)) {
            quantityPerAreaUnit = BigDecimal.valueOf(seedsPerAreaUnit.doubleValue() * thousandSeedsMass.doubleValue() / materialPurity.doubleValue() / germinationRate.doubleValue() / 100);
        }
        if (seedsPerAreaUnit.equals(BigDecimal.ZERO)) {
            seedsPerAreaUnit = BigDecimal.valueOf(quantityPerAreaUnit.doubleValue() / thousandSeedsMass.doubleValue() * materialPurity.doubleValue() * germinationRate.doubleValue() * 100);
        }

    }

    @PostConstruct
    @PostLoad
    @PostUpdate
    private void initCSt() {
        setOperationType(OperationType.SEEDING);
    }

}
