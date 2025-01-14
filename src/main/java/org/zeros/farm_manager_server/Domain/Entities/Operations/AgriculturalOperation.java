package org.zeros.farm_manager_server.Domain.Entities.Operations;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public abstract class AgriculturalOperation extends BaseEntity {

    @NonNull
    @ManyToOne
    @Builder.Default
    private Crop crop = MainCrop.NONE;

    @NonNull
    @Builder.Default
    @ManyToOne
    private FarmingMachine farmingMachine = FarmingMachine.UNDEFINED;

    @NonNull
    @Transient
    @Builder.Default
    private OperationType operationType = OperationType.NONE;

    @NonNull
    @Builder.Default
    private LocalDate dateStarted = ApplicationDefaults.UNDEFINED_DATE_MIN;

    @NonNull
    @Builder.Default
    private LocalDate dateFinished = ApplicationDefaults.UNDEFINED_DATE_MAX;

    @NonNull
    @Builder.Default
    private Boolean isExternalService = false;

    @NonNull
    @Builder.Default
    private BigDecimal externalServicePrice = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private BigDecimal fuelConsumptionPerUnit = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private BigDecimal fuelPrice = BigDecimal.ZERO;

    @NonNull
    @Builder.Default
    private Boolean isPlannedOperation = false;

}