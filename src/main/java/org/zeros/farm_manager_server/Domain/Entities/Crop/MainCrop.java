package org.zeros.farm_manager_server.Domain.Entities.Crop;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Entities.Operations.Harvest;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"harvest", "cropSales"})
@DiscriminatorValue("MAIN_CROP")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MainCrop extends Crop {

    @Transient
    public static final MainCrop NONE = MainCrop.builder()
            .fieldPart(FieldPart.NONE)
            .build();
    @OneToMany(mappedBy = "crop", cascade = CascadeType.REMOVE)
    @NonNull
    @Builder.Default
    private Set<Harvest> harvest = new HashSet<>();
    @OneToMany(mappedBy = "crop", cascade = CascadeType.REMOVE)
    @NonNull
    @Builder.Default
    private Set<CropSale> cropSales = new HashSet<>();
    @NonNull
    @Builder.Default
    private Boolean isFullySold = false;

    @Override
    public Set<AgriculturalOperation> getAllOperations() {
        Set<AgriculturalOperation> operations = super.getAllOperations();
        operations.addAll(harvest);
        return operations;
    }
}
