package org.zeros.farm_manager_server.entities.fields;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FieldPart extends DatabaseEntity {
    private String fieldPartName;
    private String description;
    private BigDecimal area;
    private Boolean isArchived;
    @ManyToOne
    private Field field;
    //@OneToMany
    //private Set<Crop> crops;

}
