package org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.OperationType;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;



@Getter
@Setter
@EqualsAndHashCode(callSuper = true,exclude = {"crop","farmingMachine"})
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public abstract class AgriculturalOperation extends DatabaseEntity {
    @NonNull
    @ManyToOne
    private Crop crop;
    @ManyToOne
    private FarmingMachine farmingMachine;
    @NonNull
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    @NonNull
    @Builder.Default
    private LocalDate dateStarted=LocalDate.MIN;
    @NonNull
    @Builder.Default
    private LocalDate dateFinished=LocalDate.MAX;
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
