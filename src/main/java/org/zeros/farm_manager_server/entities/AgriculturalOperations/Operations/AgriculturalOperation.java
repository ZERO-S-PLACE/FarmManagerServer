package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.OperationType;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crops.Crop.MainCrop;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
import org.zeros.farm_manager_server.model.ApplicationDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;



@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public abstract class AgriculturalOperation extends DatabaseEntity {
    @NonNull
    @ManyToOne
    @Builder.Default
    private Crop crop= MainCrop.NONE;
    @NonNull
    @Builder.Default
    @ManyToOne
    private FarmingMachine farmingMachine=FarmingMachine.UNDEFINED;
    @NonNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OperationType operationType=OperationType.NONE;
    @NonNull
    @Builder.Default
    private LocalDate dateStarted= ApplicationDefaults.UNDEFINED_DATE_MIN;
    @NonNull
    @Builder.Default
    private LocalDate dateFinished=ApplicationDefaults.UNDEFINED_DATE_MAX;
    @NonNull
    @Builder.Default
    private Boolean isExternalService=false;
    @NonNull
    @Builder.Default
    private BigDecimal externalServicePrice=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    private BigDecimal fuelConsumptionPerUnit=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    private BigDecimal fuelPrice=BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    private Boolean isPlannedOperation=false;


}
