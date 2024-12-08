package org.zeros.farm_manager_server.entities.agricultural_operations;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.zeros.farm_manager_server.entities.Crops.Crop;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.sql.Date;
import java.time.LocalDate;
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass

public abstract class AgriculturalOperation extends DatabaseEntity {
    @ManyToOne
    private Crop crop;
    @ManyToOne
    private FarmingMachine machine;
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    private Date dateStarted;
    private Date dateFinished;
    private Boolean isExternalService;
    private Float externalServicePrice;
    private Float fuelConsumptionPerUnit;
    private Float fuelPrice;
    private Boolean isPlannedOperation;

}
