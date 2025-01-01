package org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Subside;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude =
        {"cultivatedPlants", "seeding", "cultivations",
                "sprayApplications", "fertilizerApplications", "subsides"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_main_crop", discriminatorType = DiscriminatorType.STRING)
@SuperBuilder
public abstract class Crop extends BaseEntity {

    @NonNull
    @Builder.Default
    private Boolean workFinished = false;

    @ManyToOne
    @NonNull
    @Builder.Default
    private FieldPart fieldPart = FieldPart.NONE;

    @NonNull
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "crop_plant", joinColumns = @JoinColumn(name = "crop_id"), inverseJoinColumns = @JoinColumn(name = "plant_id"))
    private Set<Plant> cultivatedPlants = new HashSet<>();

    @NonNull
    @Builder.Default
    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Seeding> seeding = new HashSet<>();

    @NonNull
    @Builder.Default
    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Cultivation> cultivations = new HashSet<>();

    @NonNull
    @Builder.Default
    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<SprayApplication> sprayApplications = new HashSet<>();

    @NonNull
    @Builder.Default
    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<FertilizerApplication> fertilizerApplications = new HashSet<>();

    @NonNull
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "crop_subside", joinColumns = @JoinColumn(name = "crop_id"), inverseJoinColumns = @JoinColumn(name = "subside_id"))
    private Set<Subside> subsides = new HashSet<>();

    public Set<AgriculturalOperation> getAllOperations() {
        Set<AgriculturalOperation> operations = new HashSet<>();
        operations.addAll(seeding);
        operations.addAll(cultivations);
        operations.addAll(sprayApplications);
        operations.addAll(fertilizerApplications);
        return operations;
    }
}
