package org.zeros.farm_manager_server.domain.entities.fields;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.domain.entities.BaseEntity;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.crop.MainCrop;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"crops", "field"})
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FieldPart extends BaseEntity {

    @Transient
    public final static FieldPart NONE = FieldPart.builder()
            .fieldPartName("NONE")
            .field(Field.NONE)
            .build();
    @NotBlank
    @NonNull
    @Builder.Default
    private String fieldPartName = "";
    @NonNull
    @Builder.Default
    private String description = "";
    @NonNull
    @Builder.Default
    @DecimalMin("0.0")
    private BigDecimal area = BigDecimal.ZERO;
    @NonNull
    @Builder.Default
    private Boolean isArchived = false;
    @NonNull
    @Builder.Default
    @OneToMany(mappedBy = "fieldPart", cascade = CascadeType.REMOVE)
    private Set<Crop> crops = new HashSet<>();
    @NonNull
    @ManyToOne
    @Builder.Default
    private Field field = Field.NONE;

    public static FieldPart getDefaultFieldPart(Field field) {
        return FieldPart.builder().fieldPartName("WHOLE").field(field).area(field.getArea()).build();
    }

    public void addCrop(Crop crop) {
        crops.add(crop);
    }

    public Crop getActiveCrop() {
        Set<Crop> activeCrops = new HashSet<>();
        for (Crop crop : crops) {
            if (!crop.getWorkFinished()) {
                activeCrops.add(crop);
            }
        }
        if (activeCrops.isEmpty())
            return MainCrop.NONE;
        if (activeCrops.size() == 1)
            return activeCrops.stream().findFirst().get();
        throw new IllegalArgumentException("There are many active crops in this FieldPart");
    }

    public Set<Crop> getUnsoldCrops() {
        Set<Crop> unsoldCrops = new HashSet<>();
        for (Crop crop : crops) {
            if (crop instanceof MainCrop) {
                if (crop.getWorkFinished() && (!((MainCrop) crop).getIsFullySold())) {
                    unsoldCrops.add(crop);
                }
            }
        }
        return unsoldCrops;
    }

    public Set<Crop> getArchivedCrops() {
        Set<Crop> archivedCrops = new HashSet<>();
        for (Crop crop : crops) {
            if (crop instanceof MainCrop) {
                if (crop.getWorkFinished()) {
                    if (((MainCrop) crop).getIsFullySold()) {
                        archivedCrops.add(crop);
                    }
                }
            } else {
                if (crop.getWorkFinished()) {
                    archivedCrops.add(crop);
                }
            }

        }
        return archivedCrops;
    }
}
