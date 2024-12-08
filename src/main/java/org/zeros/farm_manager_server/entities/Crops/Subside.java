package org.zeros.farm_manager_server.entities.Crops;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.sql.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)

public class Subside extends DatabaseEntity {
    private String name;
    private String description;
    private Date yearOfSubside;
    private Set<Species> speciesAllowed;
    private Float subsideValuePerAreaUnit;
}
