package org.zeros.farm_manager_server.entities.Crops;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import org.zeros.farm_manager_server.entities.DatabaseEntity;

import java.sql.Date;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
/*@Entity
@Table( uniqueConstraints = {
        @UniqueConstraint(columnNames = {"species", "plant_variety"})
})*/
public class Plant extends DatabaseEntity {
    @Column(name="species", nullable = false)
    @OneToOne
    private Species plantSpecies;
    @Column(name="plant_variety", nullable = false)
    private String plantVariety;
    private Date registrationDate;
    private String productionCompany;
    private String countryOfOrigin;
}
