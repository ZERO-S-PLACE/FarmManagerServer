package org.zeros.farm_manager_server.entities.agricultural_operations;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FarmingMachine extends DatabaseEntity {
    private String producer;
    private String model;
    private String type;
    private String description;
}
