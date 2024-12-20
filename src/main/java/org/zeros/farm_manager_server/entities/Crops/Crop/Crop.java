package org.zeros.farm_manager_server.entities.Crops.Crop;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.Cultivation;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.FertilizerApplication;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.Seeding;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.SprayApplication;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Subside;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
import org.zeros.farm_manager_server.entities.fields.FieldPart;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"fieldPart","cultivatedPlants","seeding","cultivations","sprayApplications",
"fertilizerApplications","subsides"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_main_crop", discriminatorType = DiscriminatorType.STRING)
@SuperBuilder
public abstract class Crop extends DatabaseEntity {

    @ManyToOne
    @NonNull
    private FieldPart fieldPart;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "crop_plant", joinColumns = @JoinColumn(name = "crop_id"), inverseJoinColumns = @JoinColumn(name = "plant_id"))
    private Set<Plant> cultivatedPlants;

    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private Set<Seeding> seeding;

    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private Set<Cultivation> cultivations;

    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private Set<SprayApplication> sprayApplications;

    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private Set<FertilizerApplication> fertilizerApplications;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "crop_subside", joinColumns = @JoinColumn(name = "crop_id"), inverseJoinColumns = @JoinColumn(name = "subside_id"))
    private Set<Subside> subsides;

    @NonNull
    @Builder.Default
    Boolean workFinished=false;
}
