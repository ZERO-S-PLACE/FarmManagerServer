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

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"cultivatedPlants","seeding","cultivations","sprayApplications",
"fertilizerApplications","subsides"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_main_crop", discriminatorType = DiscriminatorType.STRING)
@SuperBuilder
public abstract class Crop extends DatabaseEntity {

    @ManyToOne
    @NonNull
    @Builder.Default
    private FieldPart fieldPart=FieldPart.NONE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "crop_plant", joinColumns = @JoinColumn(name = "crop_id"), inverseJoinColumns = @JoinColumn(name = "plant_id"))
    @NonNull
    @Builder.Default
    private Set<Plant> cultivatedPlants=new HashSet<>();

    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER,cascade = {CascadeType.REMOVE,CascadeType.PERSIST})
    @NonNull
    @Builder.Default
    private Set<Seeding> seeding=new HashSet<>();

    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    @NonNull
    @Builder.Default
    private Set<Cultivation> cultivations=new HashSet<>();


    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    @NonNull
    @Builder.Default
    private Set<SprayApplication> sprayApplications=new HashSet<>();

    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    @NonNull
    @Builder.Default
    private Set<FertilizerApplication> fertilizerApplications=new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "crop_subside", joinColumns = @JoinColumn(name = "crop_id"), inverseJoinColumns = @JoinColumn(name = "subside_id"))
    @NonNull
    @Builder.Default
    private Set<Subside> subsides=new HashSet<>();
    @NonNull
    @Builder.Default
    Boolean workFinished=false;
}
