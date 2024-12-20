package org.zeros.farm_manager_server.entities.Crops.Crop;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.Harvest;
import org.zeros.farm_manager_server.entities.Crops.CropSale;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true,exclude = {"harvest","cropSales"})
@DiscriminatorValue("MAIN_CROP")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MainCrop extends Crop {

    @OneToMany(mappedBy = "crop", fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private Set<Harvest> harvest;

    @OneToMany(mappedBy = "crop")
    private Set<CropSale> cropSales;
    @NonNull
    @Builder.Default
    private Boolean isFullySold=false;


}
