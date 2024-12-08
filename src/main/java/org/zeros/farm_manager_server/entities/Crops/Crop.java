package org.zeros.farm_manager_server.entities.Crops;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.zeros.farm_manager_server.entities.DatabaseEntity;
import org.zeros.farm_manager_server.entities.agricultural_operations.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "crop_type")
public abstract class Crop extends DatabaseEntity {

    @OneToMany
    private  Set<Plant> cultivatedPlants;
    @OneToOne
    private Seeding seeding;
    @OneToMany
    private Set<Cultivation> cultivations;
    @OneToMany
    private Set<SprayApplication> sprayApplications;
    @OneToMany
    private Set<FertilizerApplication> fertilizerApplications;
    @OneToMany
    private Set<Subside> subsides;
}
