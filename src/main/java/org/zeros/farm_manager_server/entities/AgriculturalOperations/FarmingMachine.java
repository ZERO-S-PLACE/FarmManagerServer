package org.zeros.farm_manager_server.entities.AgriculturalOperations;

import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

//@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FarmingMachine extends DatabaseEntity {
    private String producer;
    private String model;
    private String type;
    private String description;
}
