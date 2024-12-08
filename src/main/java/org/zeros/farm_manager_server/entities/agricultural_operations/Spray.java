package org.zeros.farm_manager_server.entities.agricultural_operations;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Spray extends DatabaseEntity {
    private String sprayName;
    private String producer;
    @Enumerated(EnumType.STRING)
    private SprayType sprayType;
    private String active_substances;
    private String description;

}
